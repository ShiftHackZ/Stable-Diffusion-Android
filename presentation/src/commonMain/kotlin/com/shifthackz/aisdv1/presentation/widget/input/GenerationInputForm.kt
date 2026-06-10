package com.shifthackz.aisdv1.presentation.widget.input

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.core.localization.Localization
import com.shifthackz.aisdv1.core.common.math.roundTo
import com.shifthackz.aisdv1.core.model.asString
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.domain.entity.OpenAiModel
import com.shifthackz.aisdv1.domain.entity.OpenAiQuality
import com.shifthackz.aisdv1.domain.entity.OpenAiSize
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.entity.StabilityAiClipGuidance
import com.shifthackz.aisdv1.domain.entity.StabilityAiSampler
import com.shifthackz.aisdv1.domain.entity.StabilityAiStylePreset
import com.shifthackz.aisdv1.presentation.theme.sliderColors
import com.shifthackz.aisdv1.presentation.theme.textFieldColors
import com.shifthackz.aisdv1.presentation.widget.input.GenerationInputFormConstants.BATCH_RANGE_MAX
import com.shifthackz.aisdv1.presentation.widget.input.GenerationInputFormConstants.BATCH_RANGE_MIN
import com.shifthackz.aisdv1.presentation.widget.input.GenerationInputFormConstants.CFG_SCALE_RANGE_MAX
import com.shifthackz.aisdv1.presentation.widget.input.GenerationInputFormConstants.CFG_SCALE_RANGE_MIN
import com.shifthackz.aisdv1.presentation.widget.input.GenerationInputFormConstants.SAMPLING_STEPS_LOCAL_DIFFUSION_MAX
import com.shifthackz.aisdv1.presentation.widget.input.GenerationInputFormConstants.SAMPLING_STEPS_RANGE_MAX
import com.shifthackz.aisdv1.presentation.widget.input.GenerationInputFormConstants.SAMPLING_STEPS_RANGE_MIN
import com.shifthackz.aisdv1.presentation.widget.input.GenerationInputFormConstants.SAMPLING_STEPS_RANGE_STABILITY_AI_MAX
import com.shifthackz.aisdv1.presentation.widget.input.GenerationInputFormConstants.SUB_SEED_STRENGTH_MAX
import com.shifthackz.aisdv1.presentation.widget.input.GenerationInputFormConstants.SUB_SEED_STRENGTH_MIN
import com.shifthackz.aisdv1.presentation.widget.engine.EngineSelectionComponent
import com.shifthackz.aisdv1.presentation.widget.input.chip.ChipTextFieldEvent
import com.shifthackz.aisdv1.presentation.widget.input.chip.ChipTextFieldWithItem
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.roundToInt
import kotlin.random.Random

@Composable
fun GenerationInputForm(
    modifier: Modifier = Modifier,
    state: GenerationInputFormState,
    isImg2Img: Boolean = false,
    textFieldContainerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    promptChipTextFieldState: MutableState<TextFieldValue>,
    negativePromptChipTextFieldState: MutableState<TextFieldValue>,
    onEvent: (GenerationInputFormEvent) -> Unit = {},
    afterSlidersSection: @Composable () -> Unit = {},
) {
    Column(modifier = modifier) {
        if (!state.onBoardingDemo) {
            when (state.mode) {
                ServerSource.AUTOMATIC1111,
                ServerSource.SWARM_UI,
                ServerSource.STABILITY_AI,
                ServerSource.HUGGING_FACE,
                ServerSource.LOCAL_MICROSOFT_ONNX -> EngineSelectionComponent(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                )

                ServerSource.OPEN_AI -> DropdownTextField(
                    modifier = Modifier.padding(top = 8.dp),
                    label = Localization.string("hint_model_open_ai").asUiText(),
                    value = state.openAiModel,
                    items = OpenAiModel.entries,
                    onItemSelected = { onEvent(GenerationInputFormEvent.UpdateOpenAiModel(it)) },
                    displayDelegate = { it.alias.asUiText() },
                )

                else -> Unit
            }
        }
        if (state.formPromptTaggedInput) {
            ChipTextFieldWithItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                textFieldValueState = promptChipTextFieldState,
                label = Localization.string("hint_prompt").asUiText(),
                list = state.promptKeywords,
                onItemClick = { _, tag ->
                    onEvent(
                        GenerationInputFormEvent.EditTag(
                            prompt = state.prompt,
                            negativePrompt = state.negativePrompt,
                            tag = tag,
                            isNegative = false,
                        )
                    )
                },
            ) { event ->
                val prompt = processTaggedPrompt(state.promptKeywords, event)
                onEvent(GenerationInputFormEvent.UpdatePrompt(prompt))
            }
        } else {
            PlatformOutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                value = state.prompt,
                onValueChange = { onEvent(GenerationInputFormEvent.UpdatePrompt(it)) },
                label = Localization.string("hint_prompt"),
                containerColor = textFieldContainerColor,
                textColor = MaterialTheme.colorScheme.onSurface,
                hintColor = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        // Horde does not support "negative prompt"
        when (state.mode) {
            ServerSource.AUTOMATIC1111,
            ServerSource.SWARM_UI,
            ServerSource.HUGGING_FACE,
            ServerSource.STABILITY_AI,
            ServerSource.LOCAL_MICROSOFT_ONNX -> {
                if (state.formPromptTaggedInput) {
                    ChipTextFieldWithItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        textFieldValueState = negativePromptChipTextFieldState,
                        label = Localization.string("hint_prompt_negative").asUiText(),
                        list = state.negativePromptKeywords,
                        onItemClick = { _, tag ->
                            onEvent(
                                GenerationInputFormEvent.EditTag(
                                    prompt = state.prompt,
                                    negativePrompt = state.negativePrompt,
                                    tag = tag,
                                    isNegative = true,
                                )
                            )
                        },
                    ) { event ->
                        val prompt = processTaggedPrompt(state.negativePromptKeywords, event)
                        onEvent(GenerationInputFormEvent.UpdateNegativePrompt(prompt))
                    }
                } else {
                    PlatformOutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        value = state.negativePrompt,
                        onValueChange = {
                            onEvent(GenerationInputFormEvent.UpdateNegativePrompt(it))
                        },
                        label = Localization.string("hint_prompt_negative"),
                        containerColor = textFieldContainerColor,
                        textColor = MaterialTheme.colorScheme.onSurface,
                        hintColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            else -> Unit
        }

        // Size input fields
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
        ) {
            val localModifier = Modifier.weight(1f)

            when (state.mode) {
                ServerSource.HORDE,
                ServerSource.LOCAL_MICROSOFT_ONNX -> {
                    DropdownTextField(
                        modifier = localModifier.padding(end = 4.dp),
                        label = Localization.string("width").asUiText(),
                        value = state.width,
                        items = GenerationInputFormConstants.sizes,
                        onItemSelected = { onEvent(GenerationInputFormEvent.UpdateWidth(it)) },
                    )
                    DropdownTextField(
                        modifier = localModifier.padding(start = 4.dp),
                        label = Localization.string("height").asUiText(),
                        value = state.height,
                        items = GenerationInputFormConstants.sizes,
                        onItemSelected = { onEvent(GenerationInputFormEvent.UpdateHeight(it)) },
                    )
                }

                ServerSource.AUTOMATIC1111,
                ServerSource.SWARM_UI,
                ServerSource.HUGGING_FACE -> {
                    GenerationSizeTextFieldsComponent(modifier = localModifier, state = state, onEvent = onEvent)
                }

                ServerSource.STABILITY_AI -> {
                    if (isImg2Img) Unit
                    else GenerationSizeTextFieldsComponent(modifier = localModifier, state = state, onEvent = onEvent)
                }

                ServerSource.OPEN_AI -> {
                    DropdownTextField(
                        label = Localization.string("hint_image_size").asUiText(),
                        value = state.openAiSize,
                        items = OpenAiSize.entries.filter {
                            it.supportedModels.contains(state.openAiModel)
                        },
                        onItemSelected = { onEvent(GenerationInputFormEvent.UpdateOpenAiSize(it)) },
                        displayDelegate = { it.key.asUiText() },
                    )
                }
                else -> Unit
            }
        }

        if (state.mode == ServerSource.OPEN_AI) {
            DropdownTextField(
                modifier = Modifier.padding(top = 8.dp),
                label = Localization.string("hint_quality").asUiText(),
                value = state.openAiQuality,
                items = OpenAiQuality.entries,
                onItemSelected = { onEvent(GenerationInputFormEvent.UpdateOpenAiQuality(it)) },
                displayDelegate = { it.key.asUiText() },
            )
            GenerationBatchComponent(state = state, onEvent = onEvent)
        }

        if (state.advancedToggleButtonVisible && state.mode != ServerSource.OPEN_AI) {
            TextButton(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = {
                    onEvent(
                        GenerationInputFormEvent.UpdateAdvancedOptionsVisibility(
                            !state.advancedOptionsVisible,
                        )
                    )
                },
            ) {
                Icon(
                    imageVector = if (state.advancedOptionsVisible) Icons.Default.ArrowDropUp
                    else Icons.Default.ArrowDropDown,
                    contentDescription = null,
                )
                Text(
                    text = Localization.string(
                        if (state.advancedOptionsVisible) "action_options_hide"
                        else "action_options_show",
                    )
                )
            }
        }

        GenerationInputAdvancedOptions(
            state = state,
            onEvent = onEvent,
            afterSlidersSection = afterSlidersSection,
        )
    }
}
