package com.example.rangegen.ui.viewmodel

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.rangegen.data.model.RangeHistory
import com.example.rangegen.data.model.RangeMode
import com.example.rangegen.data.model.UserSettings
import com.example.rangegen.data.repository.HistoryRepository
import com.example.rangegen.data.repository.SettingsRepository
import com.example.rangegen.domain.math.DirectValueCalculator
import com.example.rangegen.domain.math.PointsRangeCalculator
import com.example.rangegen.domain.math.RangeCalculator
import com.example.rangegen.domain.math.RangeFormatter
import com.example.rangegen.domain.math.RangeGenParser
import com.example.rangegen.domain.math.RangeValidator
import com.example.rangegen.domain.math.ValidationResult
import com.example.rangegen.util.HapticsHelper
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.math.BigDecimal

class RangeGenViewModel(application: Application) : AndroidViewModel(application) {

    private val settingsRepo = SettingsRepository(application)
    private val historyRepo = HistoryRepository(application)

    private val _uiState = MutableStateFlow(RangeGenUiState())
    val uiState: StateFlow<RangeGenUiState> = _uiState.asStateFlow()

    private var calculationJob: Job? = null

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        val settings = settingsRepo.loadSettings()
        val history = historyRepo.getHistory()
        val lastMode = settingsRepo.loadLastMode()
        val lastStart = settingsRepo.loadLastStart()
        val lastEnd = settingsRepo.loadLastEnd()
        val lastPoints = settingsRepo.loadLastPoints()
        val lastStep = settingsRepo.loadLastStep()
        val lastIncludeEnd = settingsRepo.loadLastIncludeEnd()

        _uiState.update {
            it.copy(
                mode = lastMode,
                startInput = lastStart,
                endInput = lastEnd,
                pointsInput = lastPoints,
                stepInput = lastStep,
                includeEnd = lastIncludeEnd,
                settings = settings,
                history = history
            )
        }

        // Generate initial default range
        generateRange(isInitial = true)
    }

    fun setMode(mode: RangeMode) {
        if (_uiState.value.mode != mode) {
            _uiState.update { it.copy(mode = mode, errorMessage = null, warningMessage = null) }
            HapticsHelper.performHaptic(getApplication(), _uiState.value.settings.hapticFeedback)
        }
    }

    fun setStartInput(value: String) {
        _uiState.update { it.copy(startInput = value, errorMessage = null) }
    }

    fun setEndInput(value: String) {
        _uiState.update { it.copy(endInput = value, errorMessage = null) }
    }

    fun setPointsInput(value: String) {
        _uiState.update { it.copy(pointsInput = value, errorMessage = null) }
    }

    fun setStepInput(value: String) {
        _uiState.update { it.copy(stepInput = value, errorMessage = null) }
    }

    fun setIncludeEnd(includeEnd: Boolean) {
        if (_uiState.value.includeEnd != includeEnd) {
            _uiState.update { it.copy(includeEnd = includeEnd, errorMessage = null) }
            HapticsHelper.performHaptic(getApplication(), _uiState.value.settings.hapticFeedback)
        }
    }

    fun toggleFormulaExpanded() {
        _uiState.update { it.copy(isFormulaExpanded = !it.isFormulaExpanded) }
    }

    fun setLookupIndexInput(input: String) {
        _uiState.update { it.copy(lookupIndexInput = input, lookupError = null) }
    }

    fun calculateDirectValue() {
        val currentState = _uiState.value
        val result = currentState.result ?: return

        val parsedIndex = RangeGenParser.parseLookupIndex(currentState.lookupIndexInput)
        if (parsedIndex.isFailure) {
            val err = parsedIndex.exceptionOrNull()?.message ?: "Enter a valid index."
            _uiState.update { it.copy(lookupError = err, lookupResult = null) }
            HapticsHelper.performHaptic(getApplication(), currentState.settings.hapticFeedback, isError = true)
            return
        }

        val index = parsedIndex.getOrThrow()
        try {
            val value = DirectValueCalculator.calculateValueAtIndex(
                start = result.start,
                interval = result.interval,
                index = index
            )
            _uiState.update { it.copy(lookupResult = value, lookupError = null) }
            HapticsHelper.performHaptic(getApplication(), currentState.settings.hapticFeedback)
        } catch (e: Exception) {
            _uiState.update { it.copy(lookupError = e.message ?: "Calculation error", lookupResult = null) }
            HapticsHelper.performHaptic(getApplication(), currentState.settings.hapticFeedback, isError = true)
        }
    }

    fun generateRange(isInitial: Boolean = false) {
        calculationJob?.cancel()

        val currentState = _uiState.value
        val mode = currentState.mode
        val startRaw = currentState.startInput
        val endRaw = currentState.endInput
        val pointsOrStepRaw = if (mode == RangeMode.BY_POINTS) currentState.pointsInput else currentState.stepInput
        val includeEnd = currentState.includeEnd

        val validation = RangeValidator.validate(mode, startRaw, endRaw, pointsOrStepRaw)
        if (validation is ValidationResult.Error) {
            _uiState.update {
                it.copy(
                    errorMessage = validation.message,
                    warningMessage = null,
                    result = null,
                    isCalculating = false
                )
            }
            if (!isInitial) {
                HapticsHelper.performHaptic(getApplication(), currentState.settings.hapticFeedback, isError = true)
            }
            return
        }

        if (!isInitial) {
            HapticsHelper.performHaptic(getApplication(), currentState.settings.hapticFeedback)
        }

        _uiState.update {
            it.copy(
                isCalculating = true,
                errorMessage = null,
                warningMessage = null,
                lookupResult = null,
                lookupError = null
            )
        }

        calculationJob = viewModelScope.launch {
            try {
                val calculationResult = withContext(Dispatchers.Default) {
                    withTimeoutOrNull(5000L) {
                        when (validation) {
                            is ValidationResult.SuccessByPoints -> {
                                RangeCalculator.generateByPoints(
                                    start = validation.start,
                                    end = validation.end,
                                    points = validation.points,
                                    includeEnd = includeEnd,
                                    mathContext = PointsRangeCalculator.DEFAULT_MATH_CONTEXT
                                )
                            }

                            is ValidationResult.SuccessByStep -> {
                                RangeCalculator.generateByStep(
                                    start = validation.start,
                                    end = validation.end,
                                    step = validation.step,
                                    includeEnd = includeEnd,
                                    mathContext = PointsRangeCalculator.DEFAULT_MATH_CONTEXT
                                )
                            }

                            else -> null
                        }
                    }
                }

                if (calculationResult == null) {
                    // Timeout (5 seconds exceeded)
                    _uiState.update {
                        it.copy(
                            isCalculating = false,
                            result = null,
                            errorMessage = "Calculation took too long. Try a smaller range or fewer values."
                        )
                    }
                    return@launch
                }

                if (calculationResult.isSuccess) {
                    val rangeResult = calculationResult.getOrThrow()
                    val warning = if (mode == RangeMode.BY_STEP && !rangeResult.endReached) {
                        "The selected step does not land exactly on the end value."
                    } else null

                    _uiState.update {
                        it.copy(
                            isCalculating = false,
                            result = rangeResult,
                            errorMessage = null,
                            warningMessage = warning
                        )
                    }

                    // Save last inputs
                    settingsRepo.saveLastInputs(
                        mode = mode,
                        start = startRaw,
                        end = endRaw,
                        points = currentState.pointsInput,
                        step = currentState.stepInput,
                        includeEnd = includeEnd
                    )
                } else {
                    val err = calculationResult.exceptionOrNull()?.message ?: "Calculation error."
                    _uiState.update {
                        it.copy(
                            isCalculating = false,
                            result = null,
                            errorMessage = err
                        )
                    }
                }
            } catch (_: CancellationException) {
                _uiState.update {
                    it.copy(
                        isCalculating = false,
                        errorMessage = "Calculation cancelled."
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isCalculating = false,
                        result = null,
                        errorMessage = e.message ?: "An unexpected error occurred."
                    )
                }
            }
        }
    }

    fun cancelCalculation() {
        calculationJob?.cancel()
        _uiState.update {
            it.copy(
                isCalculating = false,
                errorMessage = "Calculation cancelled."
            )
        }
    }

    fun clearInputs() {
        calculationJob?.cancel()
        _uiState.update {
            it.copy(
                startInput = "",
                endInput = "",
                pointsInput = "",
                stepInput = "",
                result = null,
                errorMessage = null,
                warningMessage = null,
                lookupIndexInput = "",
                lookupResult = null,
                lookupError = null,
                isCalculating = false
            )
        }
        HapticsHelper.performHaptic(getApplication(), _uiState.value.settings.hapticFeedback)
    }

    fun saveCurrentRangeToHistory() {
        val currentState = _uiState.value
        val result = currentState.result ?: return

        val pointsOrStep = if (result.mode == RangeMode.BY_POINTS) {
            currentState.pointsInput.ifEmpty { result.requestedPointsOrStep }
        } else {
            currentState.stepInput.ifEmpty { result.requestedPointsOrStep }
        }

        val added = historyRepo.addHistoryItem(
            mode = result.mode,
            start = currentState.startInput,
            end = currentState.endInput,
            pointsOrStep = pointsOrStep,
            includeEnd = result.includeEnd
        )

        val updatedHistory = historyRepo.getHistory()
        val message = if (added) "Saved to history." else "Already saved in history."
        _uiState.update { it.copy(history = updatedHistory, snackbarMessage = message) }
        HapticsHelper.performHaptic(getApplication(), currentState.settings.hapticFeedback)
    }

    fun restoreFromHistory(item: RangeHistory) {
        _uiState.update {
            it.copy(
                mode = item.mode,
                startInput = item.start,
                endInput = item.end,
                pointsInput = if (item.mode == RangeMode.BY_POINTS) item.pointsOrStep else it.pointsInput,
                stepInput = if (item.mode == RangeMode.BY_STEP) item.pointsOrStep else it.stepInput,
                includeEnd = item.includeEnd,
                errorMessage = null,
                warningMessage = null
            )
        }
        HapticsHelper.performHaptic(getApplication(), _uiState.value.settings.hapticFeedback)
        generateRange()
    }

    fun deleteHistoryItem(id: String) {
        historyRepo.deleteItem(id)
        val updatedHistory = historyRepo.getHistory()
        _uiState.update { it.copy(history = updatedHistory, snackbarMessage = "Range deleted.") }
        HapticsHelper.performHaptic(getApplication(), _uiState.value.settings.hapticFeedback)
    }

    fun clearHistory() {
        historyRepo.clearHistory()
        _uiState.update { it.copy(history = emptyList(), snackbarMessage = "History cleared.") }
        HapticsHelper.performHaptic(getApplication(), _uiState.value.settings.hapticFeedback)
    }

    fun updateSettings(newSettings: UserSettings) {
        settingsRepo.saveSettings(newSettings)
        _uiState.update { it.copy(settings = newSettings) }
    }

    fun resetSettings() {
        val defaults = settingsRepo.resetSettings()
        _uiState.update { it.copy(settings = defaults, snackbarMessage = "Settings reset to defaults.") }
        HapticsHelper.performHaptic(getApplication(), defaults.hapticFeedback)
    }

    fun copyResult(context: Context) {
        val result = _uiState.value.result ?: return
        val text = RangeFormatter.formatCopyShareText(
            result = result,
            decimalPlaces = _uiState.value.settings.decimalPlaces,
            roundingMode = _uiState.value.settings.roundingMode.roundingMode
        )

        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("RangeGen Results", text)
        clipboard.setPrimaryClip(clip)

        _uiState.update { it.copy(snackbarMessage = "Copied to clipboard.") }
        HapticsHelper.performHaptic(getApplication(), _uiState.value.settings.hapticFeedback)
    }

    fun shareResult(context: Context) {
        val result = _uiState.value.result ?: return
        val text = RangeFormatter.formatCopyShareText(
            result = result,
            decimalPlaces = _uiState.value.settings.decimalPlaces,
            roundingMode = _uiState.value.settings.roundingMode.roundingMode
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "RangeGen Numerical Range")
            putExtra(Intent.EXTRA_TEXT, text)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        context.startActivity(Intent.createChooser(intent, "Share Range Results").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })

        HapticsHelper.performHaptic(getApplication(), _uiState.value.settings.hapticFeedback)
    }

    fun clearSnackbar() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }
}
