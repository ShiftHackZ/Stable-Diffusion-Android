package com.shifthackz.aisdv1.presentation.screen.debug

import com.shifthackz.aisdv1.core.common.file.FileProviderDescriptor
import com.shifthackz.aisdv1.core.common.log.FileLoggingTree
import com.shifthackz.aisdv1.core.common.log.errorLog
import com.shifthackz.aisdv1.core.common.schedulers.SchedulersProvider
import com.shifthackz.aisdv1.core.common.schedulers.subscribeOnMainThread
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.core.viewmodel.MviRxViewModel
import com.shifthackz.aisdv1.domain.feature.work.BackgroundTaskManager
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.usecase.debug.DebugInsertBadBase64UseCase
import com.shifthackz.aisdv1.presentation.model.Modal
import com.shifthackz.aisdv1.presentation.navigation.router.main.MainRouter
import io.reactivex.rxjava3.kotlin.subscribeBy
import com.shifthackz.aisdv1.core.localization.R as LocalizationR

class DebugMenuViewModel(
    private val preferenceManager: PreferenceManager,
    private val fileProviderDescriptor: FileProviderDescriptor,
    private val debugInsertBadBase64UseCase: DebugInsertBadBase64UseCase,
    private val schedulersProvider: SchedulersProvider,
    private val mainRouter: MainRouter,
    private val backgroundTaskManager: BackgroundTaskManager,
) : MviRxViewModel<DebugMenuState, DebugMenuIntent, DebugMenuEffect>() {

    override val initialState = DebugMenuState()

    init {
        !preferenceManager
            .observe()
            .subscribeOnMainThread(schedulersProvider)
            .subscribeBy(::errorLog) { settings ->
                updateState { state ->
                    state.copy(
                        localDiffusionAllowCancel = settings.localDiffusionAllowCancel,
                        localDiffusionSchedulerThread = settings.localDiffusionSchedulerThread,
                    )
                }
            }
    }

    override fun processIntent(intent: DebugMenuIntent) {
        when (intent) {
            DebugMenuIntent.NavigateBack -> mainRouter.navigateBack()

            DebugMenuIntent.InsertBadBase64 -> !debugInsertBadBase64UseCase()
                .subscribeOnMainThread(schedulersProvider)
                .subscribeBy(::onError, ::onSuccess)

            DebugMenuIntent.ClearLogs -> {
                try {
                    FileLoggingTree.clearLog(fileProviderDescriptor)
                    onSuccess()
                } catch (e: Exception) {
                    onError(e)
                }
            }

            DebugMenuIntent.ViewLogs -> mainRouter.navigateToLogger()

            DebugMenuIntent.AllowLocalDiffusionCancel -> {
                preferenceManager.localDiffusionAllowCancel = !currentState.localDiffusionAllowCancel
            }

            DebugMenuIntent.LocalDiffusionScheduler.Request -> updateState {
                it.copy(screenModal = Modal.LDScheduler(it.localDiffusionSchedulerThread))
            }

            is DebugMenuIntent.LocalDiffusionScheduler.Confirm -> {
                preferenceManager.localDiffusionSchedulerThread = intent.token
            }

            DebugMenuIntent.DismissModal -> updateState {
                it.copy(screenModal = Modal.None)
            }

            DebugMenuIntent.WorkManager.CancelAll -> backgroundTaskManager
                .cancelAll()
                .handleState()

            DebugMenuIntent.WorkManager.RestartTxt2Img -> backgroundTaskManager
                .retryLastTextToImageTask()
                .handleState()

            DebugMenuIntent.WorkManager.RestartImg2Img -> backgroundTaskManager
                .retryLastImageToImageTask()
                .handleState()
        }
    }

    private fun Result<Unit>.handleState() = this.fold(
        onSuccess = { onSuccess() },
        onFailure = ::onError,
    )

    private fun onSuccess() {
        emitEffect(DebugMenuEffect.Message(LocalizationR.string.success.asUiText()))
    }

    private fun onError(t: Throwable) {
        errorLog(t)
        emitEffect(DebugMenuEffect.Message(LocalizationR.string.failure.asUiText()))
    }
}
