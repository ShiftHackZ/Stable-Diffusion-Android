package com.shifthackz.aisdv1.presentation.modal.embedding

import com.shifthackz.aisdv1.core.common.log.debugLog
import com.shifthackz.aisdv1.core.common.log.errorLog
import com.shifthackz.aisdv1.core.common.schedulers.SchedulersProvider
import com.shifthackz.aisdv1.core.common.schedulers.subscribeOnMainThread
import com.shifthackz.aisdv1.core.ui.EmptyEffect
import com.shifthackz.aisdv1.core.viewmodel.MviRxViewModel
import com.shifthackz.aisdv1.domain.usecase.sdembedding.FetchAndGetEmbeddingsUseCase
import com.shifthackz.aisdv1.presentation.utils.ExtrasFormatter
import io.reactivex.rxjava3.kotlin.subscribeBy

class EmbeddingViewModel(
    private val fetchAndGetEmbeddingsUseCase: FetchAndGetEmbeddingsUseCase,
    private val schedulersProvider: SchedulersProvider,
) : MviRxViewModel<EmbeddingState, EmbeddingEffect>() {

    override val emptyState = EmbeddingState()

    fun updateData(prompt: String, negativePrompt: String) = !fetchAndGetEmbeddingsUseCase()
        .doOnSubscribe { updateState { it.copy(loading = true) } }
        .subscribeOnMainThread(schedulersProvider)
        .subscribeBy(
            onError = { t -> errorLog(t) },
            onSuccess = { embeddings ->
                debugLog(embeddings)
                updateState { state ->
                    state.copy(
                        loading = false,
                        prompt = prompt,
                        negativePrompt = negativePrompt,
                        embeddings = embeddings.map {
                            EmbeddingItemUi(
                                keyword = it.keyword,
                                isInPrompt = ExtrasFormatter.isEmbeddingPresentInPrompt(
                                    prompt,
                                    it.keyword,
                                ),
                                isInNegativePrompt = ExtrasFormatter.isEmbeddingPresentInPrompt(
                                    negativePrompt,
                                    it.keyword,
                                ),
                            )
                        },
                    )
                }
            }
        )


    fun changeSelector(value: Boolean) = updateState {
        it.copy(selector = value)
    }

    fun toggleItem(value: EmbeddingItemUi) = updateState { state ->
        state.copy(
            embeddings = state.embeddings.map {
                if (it.keyword != value.keyword) it
                else {
                    if (state.selector) it.copy(isInPrompt = !it.isInPrompt)
                    else it.copy(isInNegativePrompt = !it.isInNegativePrompt)
                }
            },
            prompt = if (!state.selector) state.prompt else ExtrasFormatter.toggleEmbedding(
                state.prompt,
                value.keyword,
            ),
            negativePrompt = if (state.selector) state.negativePrompt else ExtrasFormatter.toggleEmbedding(
                state.negativePrompt,
                value.keyword,
            )
        )
    }

    fun applyNewPrompts() = emitEffect(
        EmbeddingEffect.ApplyPrompts(currentState.prompt, currentState.negativePrompt)
    )
}
