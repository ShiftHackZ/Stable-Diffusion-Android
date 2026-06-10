package com.shifthackz.aisdv1.presentation.screen.history

import com.shifthackz.aisdv1.core.common.schedulers.DispatchersProvider
import com.shifthackz.aisdv1.core.mvi.BaseMviViewModel
import com.shifthackz.aisdv1.core.mvi.EmptyEffect
import com.shifthackz.aisdv1.domain.usecase.gallery.GetAllGalleryUseCase
import com.shifthackz.aisdv1.presentation.navigation.router.HistoryRouter
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.withContext

class HistoryViewModel(
    private val dispatchersProvider: DispatchersProvider,
    private val getAllGalleryUseCase: GetAllGalleryUseCase,
    private val router: HistoryRouter,
    private val onError: (Throwable) -> Unit = {},
) : BaseMviViewModel<HistoryState, HistoryIntent, EmptyEffect>(
    initialState = HistoryState(),
    effectDispatcher = dispatchersProvider.immediate,
) {

    init {
        loadHistory()
    }

    override fun processIntent(intent: HistoryIntent) {
        when (intent) {
            HistoryIntent.NavigateBack -> router.navigateBack()
            HistoryIntent.Refresh -> loadHistory()
        }
    }

    private fun loadHistory() {
        updateState { it.copy(loading = true, error = null) }
        launch(dispatchersProvider.io) {
            runCatching { getAllGalleryUseCase().filterNot { it.hidden } }
                .onSuccess { items ->
                    withContext(dispatchersProvider.immediate) {
                        updateState {
                            it.copy(
                                loading = false,
                                items = items,
                            )
                        }
                    }
                }
                .onFailure { t ->
                    if (t is CancellationException) throw t
                    withContext(dispatchersProvider.immediate) {
                        updateState {
                            it.copy(
                                loading = false,
                                error = t.message ?: "Unable to load history",
                            )
                        }
                    }
                    onError(t)
                }
        }
    }
}
