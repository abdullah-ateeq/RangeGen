package com.example.rangegen.ui.viewmodel

import com.example.rangegen.data.model.RangeHistory
import com.example.rangegen.data.model.RangeMode
import com.example.rangegen.data.model.RangeResult
import com.example.rangegen.data.model.UserSettings
import java.math.BigDecimal

data class RangeGenUiState(
    val mode: RangeMode = RangeMode.BY_POINTS,

    val startInput: String = "0",
    val endInput: String = "10",

    val pointsInput: String = "6",
    val stepInput: String = "2",

    val includeEnd: Boolean = true,

    val result: RangeResult? = null,

    val errorMessage: String? = null,
    val warningMessage: String? = null,

    val isCalculating: Boolean = false,

    val history: List<RangeHistory> = emptyList(),

    val settings: UserSettings = UserSettings(),

    // Direct value lookup (FIND VALUE)
    val lookupIndexInput: String = "",
    val lookupResult: BigDecimal? = null,
    val lookupError: String? = null,

    // Expandable formula
    val isFormulaExpanded: Boolean = false,

    // UI feedback
    val snackbarMessage: String? = null
)
