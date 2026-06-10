package com.shifthackz.aisdv1.presentation.modal.history

import com.shifthackz.aisdv1.core.common.schedulers.DispatchersProvider
import com.shifthackz.aisdv1.core.localization.Localization
import com.shifthackz.aisdv1.core.mvi.BaseMviViewModel
import com.shifthackz.aisdv1.core.mvi.EmptyEffect
import com.shifthackz.aisdv1.domain.usecase.generation.GetGenerationResultPagedUseCase
import com.shifthackz.aisdv1.presentation.screen.txt2img.decodeBase64ImageBitmap

private const val INPUT_HISTORY_FIRST_PAGE = 0
private const val INPUT_HISTORY_PAGE_SIZE = 1000

class InputHistoryViewModel(
    private val dispatchersProvider: DispatchersProvider,
    private val getGenerationResultPagedUseCase: GetGenerationResultPagedUseCase,
    private val onError: (Throwable) -> Unit = {},
) : BaseMviViewModel<InputHistoryState, InputHistoryIntent, EmptyEffect>(
    initialState = InputHistoryState(),
    effectDispatcher = dispatchersProvider.immediate,
) {

    init {
        loadPage(reset = true)
    }

    override fun processIntent(intent: InputHistoryIntent) {
        when (intent) {
            InputHistoryIntent.LoadNextPage -> loadPage(reset = false)
            InputHistoryIntent.Retry -> loadPage(reset = true)
        }
    }

    private fun loadPage(reset: Boolean) {
        val state = currentState
        if (!reset && (!state.canLoadMore || state.loading || state.loadingNextPage)) return

        val page = if (reset) INPUT_HISTORY_FIRST_PAGE else state.nextPage
        updateState {
            it.copy(
                loading = reset,
                loadingNextPage = !reset,
                items = if (reset) emptyList() else it.items,
                nextPage = if (reset) INPUT_HISTORY_FIRST_PAGE else it.nextPage,
                canLoadMore = true,
                error = null,
            )
        }

        launch(dispatchersProvider.io) {
            runCatching {
                getGenerationResultPagedUseCase(
                    limit = INPUT_HISTORY_PAGE_SIZE,
                    offset = page * INPUT_HISTORY_PAGE_SIZE,
                ).map { generation ->
                    InputHistoryItemUi(
                        generationResult = generation,
                        image = generation.image.decodeBase64ImageBitmap(),
                    )
                }
            }
                .onSuccess { pageItems ->
                    updateState { current ->
                        current.copy(
                            loading = false,
                            loadingNextPage = false,
                            items = if (reset) pageItems else current.items + pageItems,
                            nextPage = page + 1,
                            canLoadMore = pageItems.isNotEmpty(),
                            error = null,
                        )
                    }
                }
                .onFailure { t ->
                    onError(t)
                    updateState {
                        it.copy(
                            loading = false,
                            loadingNextPage = false,
                            canLoadMore = false,
                            error = Localization.string("error_generic"),
                        )
                    }
                }
        }
    }
}
