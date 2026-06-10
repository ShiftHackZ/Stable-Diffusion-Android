package com.shifthackz.aisdv1.presentation.modal.tag

import com.shifthackz.aisdv1.core.common.schedulers.DispatchersProvider
import com.shifthackz.aisdv1.core.mvi.BaseMviViewModel
import com.shifthackz.aisdv1.presentation.utils.ExtrasFormatter

class EditTagViewModel(
    dispatchersProvider: DispatchersProvider,
    prompt: String,
    negativePrompt: String,
    tag: String,
    isNegative: Boolean,
) : BaseMviViewModel<EditTagState, EditTagIntent, EditTagEffect>(
    initialState = EditTagState(),
    effectDispatcher = dispatchersProvider.immediate,
) {

    init {
        processIntent(
            EditTagIntent.InitialData(
                prompt = prompt,
                negativePrompt = negativePrompt,
                tag = tag,
                isNegative = isNegative,
            ),
        )
    }

    override fun processIntent(intent: EditTagIntent) {
        when (intent) {
            is EditTagIntent.InitialData -> updateState {
                it.copy(
                    prompt = intent.prompt,
                    negativePrompt = intent.negativePrompt,
                    originalTag = intent.tag,
                    currentTag = intent.tag,
                    extraType = ExtrasFormatter.determineExtraType(intent.tag),
                    isNegative = intent.isNegative,
                )
            }

            EditTagIntent.Close -> emitEffect(EditTagEffect.Close)

            is EditTagIntent.UpdateTag -> updateState {
                it.copy(currentTag = intent.tag)
            }

            is EditTagIntent.Value -> updateState { state ->
                state.currentValue
                    ?.let {
                        when (intent) {
                            EditTagIntent.Value.Increment -> it + EditTagConstants.EXTRA_STEP
                            EditTagIntent.Value.Decrement -> it - EditTagConstants.EXTRA_STEP
                        }
                    }
                    ?.takeIf { it in (EditTagConstants.EXTRA_MINIMUM..EditTagConstants.EXTRA_MAXIMUM) }
                    ?.let { state.currentTag.replaceExtraValue(it) }
                    ?.let { state.copy(currentTag = it) }
                    ?: state
            }

            is EditTagIntent.UpdateValue -> updateState { state ->
                state.currentValue
                    ?.let { state.currentTag.replaceExtraValue(intent.value) }
                    ?.let { state.copy(currentTag = it) }
                    ?: state
            }

            is EditTagIntent.Action -> {
                val newTag = when (intent) {
                    EditTagIntent.Action.Apply -> currentState.currentTag
                    EditTagIntent.Action.Delete -> ""
                }
                emitEffect(
                    EditTagEffect.ApplyPrompts(
                        prompt = if (!currentState.isNegative) {
                            currentState.prompt
                                .replace(currentState.originalTag, newTag)
                                .trim()
                                .trim(',')
                        } else {
                            currentState.prompt
                        },
                        negativePrompt = if (currentState.isNegative) {
                            currentState.negativePrompt
                                .replace(currentState.originalTag, newTag)
                                .trim()
                                .trim(',')
                        } else {
                            currentState.negativePrompt
                        },
                    ),
                )
            }
        }
    }
}
