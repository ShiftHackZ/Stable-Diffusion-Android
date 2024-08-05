package com.shifthackz.aisdv1.presentation.modal.embedding

import com.shifthackz.aisdv1.core.common.log.errorLog
import com.shifthackz.aisdv1.core.common.schedulers.SchedulersProvider
import com.shifthackz.aisdv1.core.common.schedulers.subscribeOnMainThread
import com.shifthackz.aisdv1.core.viewmodel.MviRxViewModel
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.usecase.sdembedding.FetchAndGetEmbeddingsUseCase
import com.shifthackz.aisdv1.presentation.modal.extras.ExtrasEffect
import com.shifthackz.aisdv1.presentation.model.ErrorState
import com.shifthackz.aisdv1.presentation.utils.ExtrasFormatter
import io.reactivex.rxjava3.kotlin.subscribeBy

class EmbeddingViewModel(
    private val fetchAndGetEmbeddingsUseCase: FetchAndGetEmbeddingsUseCase,
    private val preferenceManager: PreferenceManager,
    private val schedulersProvider: SchedulersProvider,
) : MviRxViewModel<EmbeddingState, EmbeddingIntent, ExtrasEffect>() {

    override val initialState = EmbeddingState()

    init {
        updateState {
            it.copy(source = preferenceManager.source)
        }
    }

    override fun processIntent(intent: EmbeddingIntent) {
        when (intent) {
            EmbeddingIntent.ApplyNewPrompts -> emitEffect(
                ExtrasEffect.ApplyPrompts(currentState.prompt, currentState.negativePrompt)
            )

            is EmbeddingIntent.ChangeSelector -> updateState {
                it.copy(selector = intent.flag)
            }

            EmbeddingIntent.Close -> emitEffect(ExtrasEffect.Close)

            is EmbeddingIntent.ToggleItem -> updateState { state ->
                state.copy(
                    embeddings = state.embeddings.map {
                        if (it.keyword != intent.item.keyword) it
                        else {
                            if (state.selector) it.copy(isInPrompt = !it.isInPrompt)
                            else it.copy(isInNegativePrompt = !it.isInNegativePrompt)
                        }
                    },
                    prompt = if (!state.selector) state.prompt else ExtrasFormatter.toggleEmbedding(
                        state.prompt,
                        intent.item.keyword,
                    ),
                    negativePrompt = if (state.selector) state.negativePrompt else ExtrasFormatter.toggleEmbedding(
                        state.negativePrompt,
                        intent.item.keyword,
                    )
                )
            }
        }
    }

    fun updateData(prompt: String, negativePrompt: String) = !fetchAndGetEmbeddingsUseCase()
        .doOnSubscribe {
            updateState { state ->
                state.copy(
                    loading = true,
                    source = preferenceManager.source,
                )
            }
        }
        .subscribeOnMainThread(schedulersProvider)
        .subscribeBy(
            onError = { t ->
                errorLog(t)
                updateState { it.copy(loading = false, error = ErrorState.Generic) }
            },
            onSuccess = { embeddings ->
                updateState { state ->
                    state.copy(
                        loading = false,
                        source = preferenceManager.source,
                        error = ErrorState.None,
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
}
