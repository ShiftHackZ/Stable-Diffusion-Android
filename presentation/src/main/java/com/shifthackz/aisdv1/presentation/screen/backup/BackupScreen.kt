@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package com.shifthackz.aisdv1.presentation.screen.backup

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.presentation.modal.ModalRenderer
import com.shifthackz.aisdv1.presentation.screen.backup.forms.BackupOperationForm
import com.shifthackz.aisdv1.presentation.screen.backup.forms.CreateBackupForm
import com.shifthackz.aisdv1.presentation.screen.backup.forms.RestoreBackupForm
import com.shifthackz.aisdv1.presentation.screen.setup.ServerSetupScreenTags
import com.shifthackz.aisdv1.presentation.utils.saveByteArrayToUri
import com.shifthackz.aisdv1.presentation.widget.toolbar.StepBar
import com.shifthackz.android.core.mvi.MviComponent
import org.koin.androidx.compose.koinViewModel
import com.shifthackz.aisdv1.core.localization.R as LocalizationR

@Composable
fun BackupScreen() {
    var backup by remember { mutableStateOf<ByteArray?>(null) }

    val viewModel = koinViewModel<BackupViewModel>()
    val context = LocalContext.current
    val createDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/octet-stream"),
        onResult = { uri ->
            uri ?: run {
                viewModel.processIntent(BackupIntent.OnResult.Fail)
                return@rememberLauncherForActivityResult
            }
            backup?.let {
                saveByteArrayToUri(context, uri, it)
                viewModel.processIntent(BackupIntent.OnResult.Success)
            } ?: run {
                viewModel.processIntent(BackupIntent.OnResult.Fail)
            }
            backup = null
        }
    )

    MviComponent(
        viewModel = viewModel,
        processEffect = { effect ->
            when (effect) {
                is BackupEffect.SaveBackup -> {
                    backup = effect.bytes
                    createDocumentLauncher.launch(
                        "sdai_backup_${System.currentTimeMillis()}.dat",
                    )
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
                if (!state.complete) {
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
            }
        },
        bottomBar = {
            Button(
                modifier = Modifier
                    .navigationBarsPadding()
                    .height(height = 68.dp)
                    .background(MaterialTheme.colorScheme.background)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp, top = 8.dp),
                onClick = { processIntent(BackupIntent.MainButtonClick) },
                enabled = !state.loading && when (state.step) {
                    BackupState.Step.SelectOperation -> state.operation != null
                    BackupState.Step.ProcessBackup -> when (state.operation) {
                        is BackupState.Operation.Create -> state.operation.tokens.any { it.second }
                        is BackupState.Operation.Restore -> state.backupToRestore != null
                        null -> false
                    }
                },
            ) {
                Text(
                    text = stringResource(
                        id = if (state.complete) LocalizationR.string.ok
                        else when (state.step) {
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

        AnimatedContent(
            modifier = Modifier.padding(paddingValues),
            targetState = !state.loading,
            label = "backup_state_animator",
        ) { contentVisible ->
            if (contentVisible) AnimatedContent(
                targetState = state.complete,
                label = "backup_complete_animator",
            ) { completeVisible ->
                if (completeVisible) Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Icon(
                        modifier = Modifier.size(100.dp),
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Done",
                    )
                    Text(
                        text = "Success!",
                        style = MaterialTheme.typography.headlineMedium,
                    )
                } else HorizontalPager(
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

                            is BackupState.Operation.Restore -> RestoreBackupForm(
                                state = state,
                                processIntent = processIntent,
                            )

                            else -> Unit
                        }
                    }
                }
            } else Box(
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

        LaunchedEffect(state.step) {
            pagerState.animateScrollToPage(state.step.ordinal)
        }
    }
    ModalRenderer(state.screenModal) { intent ->
        (intent as? BackupIntent)?.let(processIntent::invoke)
    }
}

@Composable
@Preview
private fun PreviewStepOperation() {
    BackupScreenContent(
        state = BackupState()
    )
}
