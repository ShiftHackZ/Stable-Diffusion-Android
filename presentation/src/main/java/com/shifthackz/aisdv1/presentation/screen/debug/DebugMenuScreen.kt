@file:OptIn(ExperimentalMaterial3Api::class)

package com.shifthackz.aisdv1.presentation.screen.debug

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TextSnippet
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CancelScheduleSend
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SettingsEthernet
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.core.common.extensions.showToast
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.core.ui.MviComponent
import com.shifthackz.aisdv1.presentation.modal.ModalRenderer
import com.shifthackz.aisdv1.presentation.widget.item.SettingsHeader
import com.shifthackz.aisdv1.presentation.widget.item.SettingsItem
import org.koin.androidx.compose.koinViewModel
import com.shifthackz.aisdv1.core.localization.R as LocalizationR

@Composable
fun DebugMenuScreen() {
    val context = LocalContext.current
    MviComponent(
        viewModel = koinViewModel<DebugMenuViewModel>(),
        processEffect = { effect ->
            when (effect) {
                is DebugMenuEffect.Message -> context.showToast(
                    effect.message.asString(context)
                )
            }
        }
    ) { state, intentHandler ->
        ScreenContent(
            modifier = Modifier.fillMaxSize(),
            state = state,
            processIntent = intentHandler,
        )
    }
}

@Composable
private fun ScreenContent(
    modifier: Modifier = Modifier,
    state: DebugMenuState = DebugMenuState(),
    processIntent: (DebugMenuIntent) -> Unit = {},
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = LocalizationR.string.title_debug_menu),
                        style = MaterialTheme.typography.headlineMedium,
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { processIntent(DebugMenuIntent.NavigateBack) },
                        content = {
                            Icon(
                                Icons.AutoMirrored.Outlined.ArrowBack,
                                contentDescription = "Back button",
                            )
                        },
                    )
                },
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
        ) {
            val headerModifier = Modifier.padding(vertical = 16.dp)
            val itemModifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)

            SettingsHeader(
                modifier = headerModifier,
                text = LocalizationR.string.debug_section_main.asUiText(),
            )
            SettingsItem(
                modifier = itemModifier,
                startIcon = Icons.AutoMirrored.Filled.TextSnippet,
                text = LocalizationR.string.debug_action_logger.asUiText(),
                onClick = { processIntent(DebugMenuIntent.ViewLogs) },
            )
            SettingsItem(
                modifier = itemModifier,
                startIcon = Icons.Default.CleaningServices,
                text = LocalizationR.string.debug_action_logger_clear.asUiText(),
                onClick = { processIntent(DebugMenuIntent.ClearLogs) },
            )

            SettingsHeader(
                modifier = headerModifier,
                text = LocalizationR.string.debug_section_work_manager.asUiText(),
            )
            SettingsItem(
                modifier = itemModifier,
                startIcon = Icons.Default.Refresh,
                text = LocalizationR.string.debug_action_work_restart_txt2img.asUiText(),
                onClick = { processIntent(DebugMenuIntent.WorkManager.RestartTxt2Img) },
            )
            SettingsItem(
                modifier = itemModifier,
                startIcon = Icons.Default.Refresh,
                text = LocalizationR.string.debug_action_work_restart_img2img.asUiText(),
                onClick = { processIntent(DebugMenuIntent.WorkManager.RestartImg2Img) },
            )
            SettingsItem(
                modifier = itemModifier,
                startIcon = Icons.Default.Cancel,
                text = LocalizationR.string.debug_action_work_cancel_all.asUiText(),
                onClick = { processIntent(DebugMenuIntent.WorkManager.CancelAll) },
            )

            SettingsHeader(
                modifier = headerModifier,
                text = LocalizationR.string.debug_section_ld.asUiText(),
            )
            SettingsItem(
                modifier = itemModifier,
                startIcon = Icons.Default.CancelScheduleSend,
                text = LocalizationR.string.debug_action_ld_allow_cancel.asUiText(),
                onClick = { processIntent(DebugMenuIntent.AllowLocalDiffusionCancel) },
                endValueContent = {
                    Switch(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        checked = state.localDiffusionAllowCancel,
                        onCheckedChange = { processIntent(DebugMenuIntent.AllowLocalDiffusionCancel) },
                    )
                }
            )
            SettingsItem(
                modifier = itemModifier,
                startIcon = Icons.Default.Construction,
                text = LocalizationR.string.debug_action_ld_scheduler.asUiText(),
                onClick = { processIntent(DebugMenuIntent.LocalDiffusionScheduler.Request) },
                endValueText = state.localDiffusionSchedulerThread.mapToUi(),
            )

            SettingsHeader(
                modifier = headerModifier,
                text = LocalizationR.string.debug_section_qa.asUiText(),
            )
            SettingsItem(
                modifier = itemModifier,
                startIcon = Icons.Default.SettingsEthernet,
                text = LocalizationR.string.debug_action_bad_base64.asUiText(),
                onClick = { processIntent(DebugMenuIntent.InsertBadBase64) },
            )
        }
        ModalRenderer(screenModal = state.screenModal) {
            (it as? DebugMenuIntent)?.let(processIntent::invoke)
        }
    }
}

@Composable
@Preview(showSystemUi = true, showBackground = true)
private fun DebugMenuScreenPreview() {
    ScreenContent()
}
