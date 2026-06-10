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

/**
 * Renders the `GenerationBatchComponent` UI for the SDAI presentation layer.
 *
 * @param state state rendered or processed by the component.
 * @param onEvent callback invoked by the component.
 * @author Dmitriy Moroz
 */
@Composable
internal fun GenerationBatchComponent(
    state: GenerationInputFormState,
    onEvent: (GenerationInputFormEvent) -> Unit,
) {
        Text(
            modifier = Modifier.padding(top = 8.dp),
            text = Localization.string("hint_batch", "${state.batchCount}"),
        )
        SliderTextInputField(
            value = state.batchCount * 1f,
            valueRange = (BATCH_RANGE_MIN * 1f)..(BATCH_RANGE_MAX * 1f),
            valueDiff = 1f,
            fractionDigits = 0,
            steps = abs(BATCH_RANGE_MIN - BATCH_RANGE_MAX) - 1,
            sliderColors = sliderColors,
            onValueChange = {
                onEvent(GenerationInputFormEvent.UpdateBatch(it.roundToInt()))
            },
        )
}

/**
 * Renders the `GenerationSizeTextFieldsComponent` UI for the SDAI presentation layer.
 *
 * @param modifier Compose modifier applied to the rendered UI.
 * @param state state rendered or processed by the component.
 * @param onEvent callback invoked by the component.
 * @author Dmitriy Moroz
 */
@Composable
internal fun RowScope.GenerationSizeTextFieldsComponent(
    modifier: Modifier = Modifier,
    state: GenerationInputFormState,
    onEvent: (GenerationInputFormEvent) -> Unit,
) {
        OutlinedTextField(
            modifier = modifier.padding(end = 4.dp),
            value = state.width,
            onValueChange = { value ->
                if (value.length <= 4) {
                    onEvent(GenerationInputFormEvent.UpdateWidth(value.filter { it.isDigit() }))
                }
            },
            isError = state.widthValidationError != null,
            supportingText = {
                state.widthValidationError?.let {
                    Text(
                        it.asString(),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            label = { Text(Localization.string("width")) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = textFieldColors,
        )
        OutlinedTextField(
            modifier = modifier.padding(start = 4.dp),
            value = state.height,
            onValueChange = { value ->
                if (value.length <= 4) {
                    onEvent(GenerationInputFormEvent.UpdateHeight(value.filter { it.isDigit() }))
                }
            },
            isError = state.heightValidationError != null,
            supportingText = {
                state.heightValidationError?.let {
                    Text(
                        it.asString(),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            label = { Text(Localization.string("height")) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = textFieldColors,
        )
}

/**
 * Executes the `processTaggedPrompt` step in the SDAI presentation layer.
 *
 * @param keywords keywords value consumed by the API.
 * @param event event value consumed by the API.
 * @return Result produced by `processTaggedPrompt`.
 * @author Dmitriy Moroz
 */
internal fun processTaggedPrompt(keywords: List<String>, event: ChipTextFieldEvent<String>): String {
    val newKeywords = when (event) {
        is ChipTextFieldEvent.Add -> buildList {
            addAll(keywords)
            add(event.item)
        }

        is ChipTextFieldEvent.AddBatch -> buildList {
            addAll(keywords)
            addAll(event.items)
        }

        is ChipTextFieldEvent.Remove -> keywords.filterIndexed { i, _ -> i != event.index }
        is ChipTextFieldEvent.Update -> keywords.mapIndexed { i, s -> if (i == event.index) event.item else s }
    }
    return newKeywords.joinToString(", ")
}
