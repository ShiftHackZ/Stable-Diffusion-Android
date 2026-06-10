package com.shifthackz.aisdv1.presentation.screen.debug

import com.shifthackz.aisdv1.core.common.schedulers.DispatchersProvider
import com.shifthackz.aisdv1.core.localization.Localization
import com.shifthackz.aisdv1.core.mvi.BaseMviViewModel
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.usecase.debug.DebugInsertBadBase64UseCase
import com.shifthackz.aisdv1.presentation.navigation.router.DebugMenuRouter
import kotlinx.coroutines.flow.catch

class DebugMenuViewModel(
    private val dispatchersProvider: DispatchersProvider,
    private val preferenceManager: PreferenceManager,
    private val debugInsertBadBase64UseCase: DebugInsertBadBase64UseCase,
    private val router: DebugMenuRouter,
    private val platformActions: DebugMenuPlatformActions,
    private val onError: (Throwable) -> Unit = {},
) : BaseMviViewModel<DebugMenuState, DebugMenuIntent, DebugMenuEffect>(
    initialState = DebugMenuState(
        showWorkManagerSection = platformActions.showWorkManagerSection,
        showLocalDiffusionSection = platformActions.showLocalDiffusionSection,
    ),
    effectDispatcher = dispatchersProvider.immediate,
) {

    init {
        launch(dispatchersProvider.immediate) {
            preferenceManager
                .observe()
                .catch { onError(it) }
                .collect { settings ->
                    updateState {
                        it.copy(
                            localDiffusionAllowCancel = settings.localDiffusionAllowCancel,
                            localDiffusionSchedulerThread = settings.localDiffusionSchedulerThread,
                        )
                    }
                }
        }
    }

    override fun processIntent(intent: DebugMenuIntent) {
        when (intent) {
            DebugMenuIntent.NavigateBack -> router.navigateBack()
            DebugMenuIntent.ViewLogs -> router.navigateToLogger()
            DebugMenuIntent.InsertBadBase64 -> launch(dispatchersProvider.io) {
                runCatching { debugInsertBadBase64UseCase() }.handleState()
            }
            DebugMenuIntent.ClearLogs -> launch(dispatchersProvider.io) {
                platformActions.clearLogs().handleState()
            }
            DebugMenuIntent.AllowLocalDiffusionCancel -> {
                if (platformActions.showLocalDiffusionSection) {
                    preferenceManager.localOnnxAllowCancel = !currentState.localDiffusionAllowCancel
                }
            }
            DebugMenuIntent.LocalDiffusionScheduler.Request -> updateState {
                if (platformActions.showLocalDiffusionSection) {
                    it.copy(screenModal = DebugMenuModal.LDScheduler(it.localDiffusionSchedulerThread))
                } else {
                    it
                }
            }
            is DebugMenuIntent.LocalDiffusionScheduler.Confirm -> {
                if (platformActions.showLocalDiffusionSection) {
                    preferenceManager.localOnnxSchedulerThread = intent.token
                }
                updateState { it.copy(screenModal = DebugMenuModal.None) }
            }
            DebugMenuIntent.DismissModal -> updateState {
                it.copy(screenModal = DebugMenuModal.None)
            }
            DebugMenuIntent.WorkManager.CancelAll -> launch(dispatchersProvider.io) {
                platformActions.cancelAllWork().handleState()
            }
            DebugMenuIntent.WorkManager.RestartTxt2Img -> launch(dispatchersProvider.io) {
                platformActions.retryLastTextToImageTask().handleState()
            }
            DebugMenuIntent.WorkManager.RestartImg2Img -> launch(dispatchersProvider.io) {
                platformActions.retryLastImageToImageTask().handleState()
            }
        }
    }

    private fun Result<Unit>.handleState() = fold(
        onSuccess = { onSuccess() },
        onFailure = ::onErrorMessage,
    )

    private fun onSuccess() {
        emitEffect(DebugMenuEffect.Message(Localization.string("success")))
    }

    private fun onErrorMessage(t: Throwable) {
        onError(t)
        emitEffect(DebugMenuEffect.Message(Localization.string("failure")))
    }
}
