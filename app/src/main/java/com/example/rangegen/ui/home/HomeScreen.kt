package com.example.rangegen.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rangegen.data.model.RangeMode
import com.example.rangegen.ui.viewmodel.RangeGenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: RangeGenViewModel,
    onNavigateToHistory: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToAbout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbar()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "RangeGen",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Generate evenly spaced numbers.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToHistory) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "View calculation history"
                        )
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings"
                        )
                    }
                    IconButton(onClick = onNavigateToAbout) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "About RangeGen"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // Mode selector: BY POINTS / BY STEP
            ModeSelector(
                selectedMode = uiState.mode,
                onModeSelected = { viewModel.setMode(it) }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Start Value Input
            NumberInputField(
                label = "Start Value",
                value = uiState.startInput,
                onValueChange = { viewModel.setStartInput(it) },
                placeholder = "0",
                contentDescriptionText = "Starting value.",
                imeAction = ImeAction.Next
            )

            Spacer(modifier = Modifier.height(14.dp))

            // End Value Input
            NumberInputField(
                label = "End Value",
                value = uiState.endInput,
                onValueChange = { viewModel.setEndInput(it) },
                placeholder = "10",
                contentDescriptionText = "Ending value.",
                imeAction = ImeAction.Next
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Mode-specific input
            when (uiState.mode) {
                RangeMode.BY_POINTS -> {
                    NumberInputField(
                        label = "Number of Points",
                        value = uiState.pointsInput,
                        onValueChange = { viewModel.setPointsInput(it) },
                        placeholder = "6",
                        contentDescriptionText = "Number of points.",
                        isIntegerOnly = true,
                        imeAction = ImeAction.Done,
                        onImeAction = {
                            focusManager.clearFocus()
                            viewModel.generateRange()
                        }
                    )
                }

                RangeMode.BY_STEP -> {
                    NumberInputField(
                        label = "Step",
                        value = uiState.stepInput,
                        onValueChange = { viewModel.setStepInput(it) },
                        placeholder = "2",
                        contentDescriptionText = "Spacing between values.",
                        imeAction = ImeAction.Done,
                        onImeAction = {
                            focusManager.clearFocus()
                            viewModel.generateRange()
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Endpoints: Include End / Exclude End
            EndpointsSelector(
                includeEnd = uiState.includeEnd,
                onIncludeEndChanged = { viewModel.setIncludeEnd(it) }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Action Buttons Row: GENERATE RANGE & CLEAR
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        focusManager.clearFocus()
                        viewModel.generateRange()
                    },
                    shape = RoundedCornerShape(12.dp),
                    enabled = !uiState.isCalculating,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .semantics { contentDescription = "Generate evenly spaced range." }
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "GENERATE RANGE",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                OutlinedButton(
                    onClick = {
                        focusManager.clearFocus()
                        viewModel.clearInputs()
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.height(52.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("CLEAR")
                }
            }

            // Calculating indicator with cancel button
            if (uiState.isCalculating) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.5.dp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Calculating…",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    OutlinedButton(
                        onClick = { viewModel.cancelCalculation() },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("CANCEL")
                    }
                }
            }

            // Error banner
            if (uiState.errorMessage != null) {
                Spacer(modifier = Modifier.height(16.dp))
                ErrorMessageCard(message = uiState.errorMessage!!)
            }

            // Warning banner
            if (uiState.warningMessage != null) {
                Spacer(modifier = Modifier.height(12.dp))
                WarningMessageCard(message = uiState.warningMessage!!)
            }

            // Results Section
            val result = uiState.result
            if (result != null) {
                Spacer(modifier = Modifier.height(24.dp))

                // Generated Range Card with preview and LazyColumn term list
                GeneratedRangeCard(
                    result = result,
                    settings = uiState.settings
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Range Summary Card
                RangeSummaryCard(
                    result = result,
                    settings = uiState.settings
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Expandable Formula Card
                ExpandableFormulaCard(
                    mode = result.mode,
                    includeEnd = result.includeEnd,
                    isExpanded = uiState.isFormulaExpanded,
                    onToggle = { viewModel.toggleFormulaExpanded() }
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Custom Term Lookup (FIND VALUE)
                FindValueCard(
                    lookupInput = uiState.lookupIndexInput,
                    onLookupInputChange = { viewModel.setLookupIndexInput(it) },
                    onCalculate = { viewModel.calculateDirectValue() },
                    lookupResult = uiState.lookupResult,
                    lookupError = uiState.lookupError,
                    settings = uiState.settings
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Action Bar: COPY, SHARE, SAVE
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FilledTonalButton(
                        onClick = { viewModel.copyResult(context) },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("COPY")
                    }

                    FilledTonalButton(
                        onClick = { viewModel.shareResult(context) },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("SHARE")
                    }

                    FilledTonalButton(
                        onClick = { viewModel.saveCurrentRangeToHistory() },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.BookmarkBorder,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("SAVE")
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
