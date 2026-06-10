package com.shifthackz.aisdv1.presentation.widget.work

import com.shifthackz.aisdv1.core.common.schedulers.DispatchersProvider
import com.shifthackz.aisdv1.core.localization.Localization
import com.shifthackz.aisdv1.core.mvi.BaseMviViewModel
import com.shifthackz.aisdv1.core.mvi.EmptyEffect
import com.shifthackz.aisdv1.domain.entity.BackgroundWorkResult
import com.shifthackz.aisdv1.domain.feature.work.BackgroundWorkObserver
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine

/**
 * Coordinates `BackgroundWorkViewModel` behavior in the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
class BackgroundWorkViewModel(
    /**
     * Exposes the `dispatchersProvider` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val dispatchersProvider: DispatchersProvider,
    /**
     * Exposes the `backgroundWorkObserver` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val backgroundWorkObserver: BackgroundWorkObserver,
    /**
     * Exposes the `imageLoader` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val imageLoader: BackgroundWorkImageLoader,
    /**
     * Exposes the `onError` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val onError: (Throwable) -> Unit = {},
) : BaseMviViewModel<BackgroundWorkState, BackgroundWorkIntent, EmptyEffect>(
    initialState = BackgroundWorkState(),
    effectDispatcher = dispatchersProvider.immediate,
) {

    init {
        launch(dispatchersProvider.immediate) {
            backgroundWorkObserver
                .observeStatus()
                .combine(backgroundWorkObserver.observeResult(), ::Pair)
                .catch { onError(it) }
                .collect { (work, result) ->
                    val resultTitle = when (result) {
                        is BackgroundWorkResult.Error -> Localization.string("notification_fail_title")
                        is BackgroundWorkResult.Success -> Localization.string("notification_finish_title")
                        else -> ""
                    }
                    (result as? BackgroundWorkResult.Success)
                        ?.ai
                        ?.firstOrNull()
                        ?.image
                        ?.also(::setImage)
                    updateState {
                        it.copy(
                            visible = work.running || result !is BackgroundWorkResult.None,
                            title = if (work.running) work.statusTitle else resultTitle,
                            subTitle = if (work.running) work.statusSubTitle else "",
                            isError = !work.running && result is BackgroundWorkResult.Error,
                            image = null,
                        )
                    }
                }
        }
    }

    override fun processIntent(intent: BackgroundWorkIntent) {
        when (intent) {
            BackgroundWorkIntent.Dismiss -> {
                updateState {
                    it.copy(
                        visible = false,
                        title = "",
                        subTitle = "",
                        isError = false,
                        image = null,
                    )
                }
                backgroundWorkObserver.dismissResult()
            }
        }
    }

    private fun setImage(base64: String) {
        launch(dispatchersProvider.io) {
            runCatching { imageLoader.load(base64) }
                .onFailure(onError)
                .onSuccess { image ->
                    updateState { it.copy(image = image) }
                }
        }
    }
}
