package com.shifthackz.aisdv1.presentation.modal.extras

import com.shifthackz.aisdv1.core.common.log.errorLog
import com.shifthackz.aisdv1.core.common.schedulers.SchedulersProvider
import com.shifthackz.aisdv1.core.common.schedulers.subscribeOnMainThread
import com.shifthackz.aisdv1.core.viewmodel.MviRxViewModel
import com.shifthackz.aisdv1.domain.entity.StableDiffusionHyperNetwork
import com.shifthackz.aisdv1.domain.entity.StableDiffusionLora
import com.shifthackz.aisdv1.domain.usecase.sdhypernet.FetchAndGetHyperNetworksUseCase
import com.shifthackz.aisdv1.domain.usecase.sdlora.FetchAndGetLorasUseCase
import com.shifthackz.aisdv1.presentation.model.ErrorState
import com.shifthackz.aisdv1.presentation.model.ExtraType
import com.shifthackz.aisdv1.presentation.utils.ExtrasFormatter
import io.reactivex.rxjava3.kotlin.subscribeBy

class ExtrasViewModel(
    private val fetchAndGetLorasUseCase: FetchAndGetLorasUseCase,
    private val fetchAndGetHyperNetworksUseCase: FetchAndGetHyperNetworksUseCase,
    private val schedulersProvider: SchedulersProvider,
) : MviRxViewModel<ExtrasState, ExtrasIntent, ExtrasEffect>() {

    override val initialState = ExtrasState()

    override fun processIntent(intent: ExtrasIntent) {
        when (intent) {
            ExtrasIntent.ApplyPrompts -> emitEffect(
                ExtrasEffect.ApplyPrompts(currentState.prompt, currentState.negativePrompt)
            )

            ExtrasIntent.Close -> emitEffect(
                ExtrasEffect.Close
            )

            is ExtrasIntent.ToggleItem -> updateState { state ->
                state.copy(
                    loras = state.loras.map {
                        if (it.key != intent.item.key) it
                        else it.copy(isApplied = !it.isApplied)
                    },
                    prompt = ExtrasFormatter.toggleExtraPromptAlias(
                        prompt = state.prompt,
                        loraAlias = intent.item.alias ?: intent.item.name,
                        type = intent.item.type,
                    ),
                )
            }
        }
    }

    fun updateData(prompt: String, negativePrompt: String, type: ExtraType) = when (type) {
        ExtraType.Lora -> fetchAndGetLorasUseCase()
        ExtraType.HyperNet -> fetchAndGetHyperNetworksUseCase()
    }
        .doOnSubscribe { updateState { it.copy(loading = true, type = type) } }
        .subscribeOnMainThread(schedulersProvider)
        .subscribeBy(
            onError = { t ->
                errorLog(t)
                updateState { it.copy(loading = false, error = ErrorState.Generic) }
            },
            onSuccess = { output ->
                updateState { state ->
                    state.copy(
                        loading = false,
                        error = ErrorState.None,
                        prompt = prompt,
                        negativePrompt = negativePrompt,
                        type = type,
                        loras = output.map {
                            val (isApplied, value) = ExtrasFormatter.isExtraWithValuePresentInPrompt(
                                prompt = prompt,
                                loraAlias = when (it) {
                                    is StableDiffusionLora -> it.alias
                                    is StableDiffusionHyperNetwork -> it.name
                                    else -> ""
                                },
                                type = type,
                            )
                            when (it) {
                                is StableDiffusionLora -> ExtraItemUi(
                                    type = type,
                                    key = "${it.name}_${type}_${System.nanoTime()}",
                                    name = it.name,
                                    alias = it.alias,
                                    isApplied = isApplied,
                                    value = value,
                                )

                                is StableDiffusionHyperNetwork -> ExtraItemUi(
                                    type = type,
                                    key = "${it.name}_${type}_${System.nanoTime()}",
                                    name = it.name,
                                    alias = null,
                                    isApplied = isApplied,
                                    value = value,
                                )

                                else -> throw IllegalStateException()
                            }
                        },
                    )
                }
            },
        )
}
