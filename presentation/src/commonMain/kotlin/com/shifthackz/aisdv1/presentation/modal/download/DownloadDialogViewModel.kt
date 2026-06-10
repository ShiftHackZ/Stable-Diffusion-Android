package com.shifthackz.aisdv1.presentation.modal.download

import com.shifthackz.aisdv1.core.common.schedulers.DispatchersProvider
import com.shifthackz.aisdv1.core.mvi.BaseMviViewModel
import com.shifthackz.aisdv1.domain.usecase.downloadable.GetLocalModelUseCase

/**
 * Coordinates `DownloadDialogViewModel` behavior in the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
class DownloadDialogViewModel(
    modelId: String,
    /**
     * Exposes the `getLocalModelUseCase` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val getLocalModelUseCase: GetLocalModelUseCase,
    /**
     * Exposes the `dispatchersProvider` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val dispatchersProvider: DispatchersProvider,
    /**
     * Exposes the `onError` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val onError: (Throwable) -> Unit = {},
) : BaseMviViewModel<DownloadDialogState, DownloadDialogIntent, DownloadDialogEffect>(
    initialState = DownloadDialogState(),
    effectDispatcher = dispatchersProvider.immediate,
) {

    init {
        loadModelData(modelId)
    }

    override fun processIntent(intent: DownloadDialogIntent) {
        when (intent) {
            is DownloadDialogIntent.SelectSource -> updateState {
                it.copy(sources = it.sources.map { (url, _) -> url to (url == intent.url) })
            }

            DownloadDialogIntent.StartDownload -> emitEffect(
                DownloadDialogEffect.StartDownload(currentState.selectedUrl),
            )

            DownloadDialogIntent.Close -> emitEffect(DownloadDialogEffect.Close)
        }
    }

    private fun loadModelData(id: String) {
        launch(dispatchersProvider.io) {
            runCatching { getLocalModelUseCase(id) }
                .onFailure(onError)
                .onSuccess { model ->
                    updateState {
                        it.copy(sources = model.sources.mapIndexed { index, url -> url to (index == 0) })
                    }
                }
        }
    }
}
