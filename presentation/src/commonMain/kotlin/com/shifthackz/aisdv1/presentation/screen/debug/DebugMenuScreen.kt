@file:OptIn(ExperimentalMaterial3Api::class)

package com.shifthackz.aisdv1.presentation.screen.debug

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.shifthackz.aisdv1.core.localization.Localization
import com.shifthackz.aisdv1.core.model.asString
import com.shifthackz.aisdv1.core.mvi.MviComponent
import com.shifthackz.aisdv1.presentation.di.initKoin
import com.shifthackz.aisdv1.presentation.modal.ldscheduler.LDSchedulerBottomSheet
import com.shifthackz.aisdv1.presentation.navigation.router.DebugMenuRouter
import org.koin.core.parameter.parametersOf

/**
 * Renders the `DebugMenuScreen` UI for the SDAI presentation layer.
 *
 * @param modifier Compose modifier applied to the rendered UI.
 * @param onMessage callback invoked by the component.
 * @param router router value consumed by the API.
 * @author Dmitriy Moroz
 */
@Composable
fun DebugMenuScreen(
    modifier: Modifier = Modifier,
    onMessage: (String) -> Unit = {},
    router: DebugMenuRouter? = null,
) {
    val koin = remember { initKoin() }
    val resolvedRouter = remember(koin, router) {
        router ?: koin.get<DebugMenuRouter>()
    }
    val platformActions = remember(koin) { koin.get<DebugMenuPlatformActions>() }
    val viewModel = remember(
        koin,
        resolvedRouter,
        platformActions,
    ) {
        koin.get<DebugMenuViewModel> {
            parametersOf(resolvedRouter, platformActions)
        }
    }
    MviComponent(
        viewModel = viewModel,
        processEffect = { effect ->
            when (effect) {
                is DebugMenuEffect.Message -> onMessage(effect.message)
            }
        },
    ) { state, intentHandler ->
        DebugMenuScreenContent(
            modifier = modifier,
            strings = debugMenuScreenStrings(),
            state = state.toContentState(),
            processAction = { action -> intentHandler(action.toIntent()) },
        )
        when (val modal = state.screenModal) {
            DebugMenuModal.None -> Unit
            is DebugMenuModal.LDScheduler -> ModalBottomSheet(
                onDismissRequest = { intentHandler(DebugMenuIntent.DismissModal) },
            ) {
                LDSchedulerBottomSheet(
                    currentScheduler = modal.scheduler,
                    onSelected = {
                        intentHandler(DebugMenuIntent.LocalDiffusionScheduler.Confirm(it))
                    },
                )
            }
        }
    }
}

/**
 * Renders the `debugMenuScreenStrings` UI for the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
@Composable
internal fun debugMenuScreenStrings() = DebugMenuScreenStrings(
    title = Localization.string("title_debug_menu"),
    mainSection = Localization.string("debug_section_main"),
    actionLogger = Localization.string("debug_action_logger"),
    actionLoggerClear = Localization.string("debug_action_logger_clear"),
    workManagerSection = Localization.string("debug_section_work_manager"),
    actionWorkRestartTxt2Img = Localization.string("debug_action_work_restart_txt2img"),
    actionWorkRestartImg2Img = Localization.string("debug_action_work_restart_img2img"),
    actionWorkCancelAll = Localization.string("debug_action_work_cancel_all"),
    localDiffusionSection = Localization.string("debug_section_ld"),
    actionLocalDiffusionAllowCancel = Localization.string("debug_action_ld_allow_cancel"),
    actionLocalDiffusionScheduler = Localization.string("debug_action_ld_scheduler"),
    qualityAssuranceSection = Localization.string("debug_section_qa"),
    actionBadBase64 = Localization.string("debug_action_bad_base64"),
    backContentDescription = Localization.string("action_back"),
)

/**
 * Renders the `toContentState` UI for the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
@Composable
internal fun DebugMenuState.toContentState() = DebugMenuScreenContentState(
    localDiffusionAllowCancel = localDiffusionAllowCancel,
    localDiffusionSchedulerThread = localDiffusionSchedulerThread.mapToUi().asString(),
    showWorkManagerSection = showWorkManagerSection,
    showLocalDiffusionSection = showLocalDiffusionSection,
    showQualityAssuranceSection = showQualityAssuranceSection,
)

/**
 * Converts SDAI data with `toIntent`.
 *
 * @author Dmitriy Moroz
 */
internal fun DebugMenuAction.toIntent(): DebugMenuIntent = when (this) {
    DebugMenuAction.NavigateBack -> DebugMenuIntent.NavigateBack
    DebugMenuAction.ViewLogs -> DebugMenuIntent.ViewLogs
    DebugMenuAction.ClearLogs -> DebugMenuIntent.ClearLogs
    DebugMenuAction.RestartTxt2Img -> DebugMenuIntent.WorkManager.RestartTxt2Img
    DebugMenuAction.RestartImg2Img -> DebugMenuIntent.WorkManager.RestartImg2Img
    DebugMenuAction.CancelAllWork -> DebugMenuIntent.WorkManager.CancelAll
    DebugMenuAction.ToggleLocalDiffusionCancel -> DebugMenuIntent.AllowLocalDiffusionCancel
    DebugMenuAction.RequestLocalDiffusionScheduler -> DebugMenuIntent.LocalDiffusionScheduler.Request
    DebugMenuAction.InsertBadBase64 -> DebugMenuIntent.InsertBadBase64
}
