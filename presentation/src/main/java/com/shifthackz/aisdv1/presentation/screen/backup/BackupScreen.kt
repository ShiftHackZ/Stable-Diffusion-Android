@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package com.shifthackz.aisdv1.presentation.screen.backup

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.core.ui.MviComponent
import com.shifthackz.aisdv1.presentation.screen.backup.forms.BackupOperationForm
import com.shifthackz.aisdv1.presentation.screen.backup.forms.CreateBackupForm
import com.shifthackz.aisdv1.presentation.widget.toolbar.StepBar
import org.koin.androidx.compose.koinViewModel
import com.shifthackz.aisdv1.core.localization.R as LocalizationR

@Composable
fun BackupScreen() {
    MviComponent(
        viewModel = koinViewModel<BackupViewModel>(),
        processEffect = { effect ->
            when (effect) {
                is BackupEffect.SaveBackup -> {

                }
            }
        },
    ) { state, processIntent ->
        BackupScreenContent(
            state = state,
            processIntent = processIntent,
        )
    }
}

@Composable
private fun BackupScreenContent(
    modifier: Modifier = Modifier,
    state: BackupState,
    processIntent: (BackupIntent) -> Unit = {},
) {
    BackHandler {
        processIntent(BackupIntent.NavigateBack)
    }
    Scaffold(
        modifier = modifier,
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = stringResource(id = LocalizationR.string.title_backup),
                            style = MaterialTheme.typography.headlineMedium,
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { processIntent(BackupIntent.NavigateBack) },
                            content = {
                                Icon(
                                    Icons.AutoMirrored.Outlined.ArrowBack,
                                    contentDescription = "Back button",
                                )
                            },
                        )
                    },
                )
                StepBar(
                    steps = BackupState.Step.entries,
                    currentStep = state.step,
                ) { step ->
                    when (step) {
                        BackupState.Step.SelectOperation -> LocalizationR.string.backup_step_operation
                        BackupState.Step.ProcessBackup -> LocalizationR.string.backup_step_process
                    }.asUiText()
                }
            }
        },
        bottomBar = {
            Button(
                modifier = Modifier
                    .height(height = 68.dp)
                    .background(MaterialTheme.colorScheme.background)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp, top = 8.dp),
                onClick = { processIntent(BackupIntent.MainButtonClick) },
                enabled = when (state.step) {
                    BackupState.Step.SelectOperation -> state.operation != null
                    BackupState.Step.ProcessBackup -> when (state.operation) {
                        is BackupState.Operation.Create -> state.operation.tokens.any { it.second }
                        is BackupState.Operation.Restore -> false
                        null -> false
                    }
                },
            ) {
                Text(
                    text = stringResource(
                        id = when (state.step) {
                            BackupState.Step.ProcessBackup -> when (state.operation) {
                                is BackupState.Operation.Create -> LocalizationR.string.backup_action_create
                                is BackupState.Operation.Restore -> LocalizationR.string.backup_action_restore
                                null -> LocalizationR.string.next
                            }

                            else -> LocalizationR.string.next
                        },
                    ),
                    color = LocalContentColor.current,
                )
            }
        },
    ) { paddingValues ->
        val pagerState = rememberPagerState(
            initialPage = 0,
            pageCount = { BackupState.Step.entries.size },
        )

        HorizontalPager(
            modifier = Modifier.padding(paddingValues),
            state = pagerState,
            userScrollEnabled = false,
        ) { index ->
            when (BackupState.Step.entries[index]) {
                BackupState.Step.SelectOperation -> BackupOperationForm(
                    state = state,
                    processIntent = processIntent,
                )

                BackupState.Step.ProcessBackup -> when (state.operation) {
                    is BackupState.Operation.Create -> CreateBackupForm(
                        state = state,
                        processIntent = processIntent,
                    )

                    is BackupState.Operation.Restore -> Unit

                    else -> Unit
                }
            }
        }

        LaunchedEffect(state.step) {
            pagerState.animateScrollToPage(state.step.ordinal)
        }
    }
}

@Composable
@Preview
private fun PreviewStepOperation() {
    BackupScreenContent(
        state = BackupState()
    )
}
