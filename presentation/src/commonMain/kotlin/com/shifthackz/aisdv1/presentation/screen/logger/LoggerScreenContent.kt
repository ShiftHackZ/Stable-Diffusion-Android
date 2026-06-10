@file:OptIn(ExperimentalMaterial3Api::class)

package com.shifthackz.aisdv1.presentation.screen.logger

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shifthackz.aisdv1.core.localization.Localization
import com.shifthackz.aisdv1.presentation.widget.scrollbar.verticalScrollbar
import kotlinx.coroutines.launch

data class LoggerScreenStrings(
    val title: String = Localization.string("title_debug_logger"),
    val emptyMessage: String = Localization.string("debug_logger_empty"),
    val backContentDescription: String = Localization.string("action_back"),
    val refreshContentDescription: String = Localization.string("action_refresh"),
    val scrollTopContentDescription: String = Localization.string("action_scroll_to_top"),
    val scrollBottomContentDescription: String = Localization.string("action_scroll_to_bottom"),
)

data class LoggerScreenContentState(
    val loading: Boolean = true,
    val text: String = "",
)

sealed interface LoggerScreenAction {
    data object ReadLogs : LoggerScreenAction
    data object NavigateBack : LoggerScreenAction
}

fun LoggerState.toContentState() = LoggerScreenContentState(
    loading = loading,
    text = text,
)

fun LoggerScreenAction.toIntent(): LoggerIntent = when (this) {
    LoggerScreenAction.ReadLogs -> LoggerIntent.ReadLogs
    LoggerScreenAction.NavigateBack -> LoggerIntent.NavigateBack
}

@Composable
fun LoggerScreenContent(
    strings: LoggerScreenStrings,
    state: LoggerScreenContentState,
    processAction: (LoggerScreenAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = { processAction(LoggerScreenAction.NavigateBack) },
                        content = {
                            Icon(
                                Icons.AutoMirrored.Outlined.ArrowBack,
                                contentDescription = strings.backContentDescription,
                            )
                        },
                    )
                },
                title = {
                    Text(
                        text = strings.title,
                        style = MaterialTheme.typography.headlineMedium,
                    )
                },
                actions = {
                    AnimatedVisibility(
                        visible = !state.loading,
                        enter = fadeIn(),
                        exit = fadeOut(),
                    ) {
                        IconButton(
                            onClick = { processAction(LoggerScreenAction.ReadLogs) },
                            content = {
                                Icon(
                                    Icons.Default.Refresh,
                                    contentDescription = strings.refreshContentDescription,
                                )
                            },
                        )
                    }
                },
            )
        },
        bottomBar = {
            AnimatedVisibility(
                visible = !state.loading && state.text.isNotBlank(),
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                Row(
                    modifier = Modifier
                        .navigationBarsPadding()
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                ) {
                    IconButton(
                        onClick = {
                            scope.launch {
                                scrollState.animateScrollTo(0)
                            }
                        },
                        content = {
                            Icon(
                                Icons.Default.ArrowUpward,
                                contentDescription = strings.scrollTopContentDescription,
                            )
                        },
                    )
                    IconButton(
                        onClick = {
                            scope.launch {
                                scrollState.animateScrollTo(scrollState.maxValue)
                            }
                        },
                        content = {
                            Icon(
                                Icons.Default.ArrowDownward,
                                contentDescription = strings.scrollBottomContentDescription,
                            )
                        },
                    )
                }
            }
        },
    ) { paddingValues ->
        val text = if (!state.loading) state.text else ""
        val scrollStateHorizontal = rememberScrollState()
        if (!state.loading && state.text.isBlank()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = strings.emptyMessage,
                    textAlign = TextAlign.Center,
                )
            }
        }
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScrollbar(scrollState)
                .verticalScroll(scrollState),
        ) {
            AnimatedVisibility(
                visible = state.loading,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(60.dp)
                            .aspectRatio(1f),
                    )
                }
            }
            Text(
                modifier = Modifier.horizontalScroll(scrollStateHorizontal),
                text = text,
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                lineHeight = 12.sp,
            )
        }
        LaunchedEffect(state.text) {
            if (!state.loading) {
                scrollState.scrollTo(scrollState.maxValue)
            }
        }
    }
}
