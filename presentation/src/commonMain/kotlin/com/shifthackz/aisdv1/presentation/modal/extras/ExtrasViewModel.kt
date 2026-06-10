package com.shifthackz.aisdv1.presentation.modal.extras

import com.shifthackz.aisdv1.core.common.schedulers.DispatchersProvider
import com.shifthackz.aisdv1.core.mvi.BaseMviViewModel
import com.shifthackz.aisdv1.domain.entity.LoRA
import com.shifthackz.aisdv1.domain.entity.StableDiffusionHyperNetwork
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.usecase.sdhypernet.FetchAndGetHyperNetworksUseCase
import com.shifthackz.aisdv1.domain.usecase.sdlora.FetchAndGetLorasUseCase
import com.shifthackz.aisdv1.presentation.model.ErrorState
import com.shifthackz.aisdv1.presentation.model.ExtraType
import com.shifthackz.aisdv1.presentation.utils.ExtrasFormatter

class ExtrasViewModel(
    private val dispatchersProvider: DispatchersProvider,
    private val fetchAndGetLorasUseCase: FetchAndGetLorasUseCase,
    private val fetchAndGetHyperNetworksUseCase: FetchAndGetHyperNetworksUseCase,
    private val preferenceManager: PreferenceManager,
    prompt: String,
    negativePrompt: String,
    type: ExtraType,
    private val onError: (Throwable) -> Unit = {},
) : BaseMviViewModel<ExtrasState, ExtrasIntent, ExtrasEffect>(
    initialState = ExtrasState(source = preferenceManager.source),
    effectDispatcher = dispatchersProvider.immediate,
) {

    init {
        loadData(prompt, negativePrompt, type)
    }

    override fun processIntent(intent: ExtrasIntent) {
        when (intent) {
            ExtrasIntent.ApplyPrompts -> emitEffect(
                ExtrasEffect.ApplyPrompts(currentState.prompt, currentState.negativePrompt),
            )

            ExtrasIntent.Close -> emitEffect(ExtrasEffect.Close)

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

    private fun loadData(prompt: String, negativePrompt: String, type: ExtraType) {
        updateState { state ->
            state.copy(
                loading = true,
                type = type,
                source = preferenceManager.source,
            )
        }

        launch(dispatchersProvider.io) {
            runCatching {
                when (type) {
                    ExtraType.Lora -> fetchAndGetLorasUseCase()
                    ExtraType.HyperNet -> fetchAndGetHyperNetworksUseCase()
                }
            }
                .onSuccess { output ->
                    updateState { state ->
                        state.copy(
                            loading = false,
                            source = preferenceManager.source,
                            error = ErrorState.None,
                            prompt = prompt,
                            negativePrompt = negativePrompt,
                            type = type,
                            loras = output.mapIndexed { index, extra ->
                                val alias = when (extra) {
                                    is LoRA -> extra.alias
                                    is StableDiffusionHyperNetwork -> extra.name
                                    else -> ""
                                }
                                val (isApplied, value) = ExtrasFormatter.isExtraWithValuePresentInPrompt(
                                    prompt = prompt,
                                    loraAlias = alias,
                                    type = type,
                                )
                                when (extra) {
                                    is LoRA -> ExtraItemUi(
                                        type = type,
                                        key = "${extra.name}_${type}_$index",
                                        name = extra.name,
                                        alias = extra.alias,
                                        isApplied = isApplied,
                                        value = value,
                                    )

                                    is StableDiffusionHyperNetwork -> ExtraItemUi(
                                        type = type,
                                        key = "${extra.name}_${type}_$index",
                                        name = extra.name,
                                        alias = null,
                                        isApplied = isApplied,
                                        value = value,
                                    )

                                    else -> error("Unsupported extra item: $extra")
                                }
                            },
                        )
                    }
                }
                .onFailure { t ->
                    updateState { it.copy(loading = false, error = ErrorState.Generic) }
                    onError(t)
                }
        }
    }
}
