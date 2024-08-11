package com.shifthackz.aisdv1.presentation.screen.debug

import com.shifthackz.aisdv1.core.common.file.FileProviderDescriptor
import com.shifthackz.aisdv1.core.common.log.FileLoggingTree
import com.shifthackz.aisdv1.core.common.log.errorLog
import com.shifthackz.aisdv1.core.common.schedulers.SchedulersProvider
import com.shifthackz.aisdv1.core.common.schedulers.subscribeOnMainThread
import com.shifthackz.aisdv1.core.viewmodel.MviRxViewModel
import com.shifthackz.aisdv1.domain.usecase.debug.DebugInsertBadBase64UseCase
import com.shifthackz.aisdv1.presentation.navigation.router.main.MainRouter
import com.shifthackz.android.core.mvi.EmptyEffect
import com.shifthackz.android.core.mvi.EmptyState
import io.reactivex.rxjava3.kotlin.subscribeBy

class DebugMenuViewModel(
    private val fileProviderDescriptor: FileProviderDescriptor,
    private val debugInsertBadBase64UseCase: DebugInsertBadBase64UseCase,
    private val schedulersProvider: SchedulersProvider,
    private val mainRouter: MainRouter,
) : MviRxViewModel<EmptyState, DebugMenuIntent, EmptyEffect>() {

    override val initialState = EmptyState

    override fun processIntent(intent: DebugMenuIntent) {
        when (intent) {
            DebugMenuIntent.NavigateBack -> mainRouter.navigateBack()

            DebugMenuIntent.InsertBadBase64 -> !debugInsertBadBase64UseCase()
                .subscribeOnMainThread(schedulersProvider)
                .subscribeBy(::errorLog)

            DebugMenuIntent.ClearLogs -> {
                FileLoggingTree.clearLog(fileProviderDescriptor)
            }

            DebugMenuIntent.ViewLogs -> mainRouter.navigateToLogger()
        }
    }
}
