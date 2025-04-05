package com.shifthackz.aisdv1.presentation.modal.download

import com.shifthackz.aisdv1.core.common.log.errorLog
import com.shifthackz.aisdv1.core.common.schedulers.DispatchersProvider
import com.shifthackz.aisdv1.core.common.schedulers.SchedulersProvider
import com.shifthackz.aisdv1.core.common.schedulers.subscribeOnMainThread
import com.shifthackz.aisdv1.core.viewmodel.MviRxViewModel
import com.shifthackz.aisdv1.domain.usecase.downloadable.GetLocalModelUseCase
import io.reactivex.rxjava3.kotlin.subscribeBy

class DownloadDialogViewModel(
    private val getLocalModelUseCase: GetLocalModelUseCase,
    private val schedulersProvider: SchedulersProvider,
    dispatchersProvider: DispatchersProvider,
) : MviRxViewModel<DownloadDialogState, DownloadDialogIntent, DownloadDialogEffect>() {

    override val initialState = DownloadDialogState()

    override val effectDispatcher = dispatchersProvider.immediate

    override fun processIntent(intent: DownloadDialogIntent) {
        when (intent) {
            is DownloadDialogIntent.LoadModelData -> !getLocalModelUseCase(intent.id)
                .subscribeOnMainThread(schedulersProvider)
                .subscribeBy(::errorLog) { model ->
                    updateState { it.copy(sources = model.sources) }
                }

            is DownloadDialogIntent.SelectSource -> emitEffect(
                DownloadDialogEffect.Select(intent.url)
            )
        }
    }
}
