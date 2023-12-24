package com.shifthackz.aisdv1.presentation.widget.input

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.core.common.math.roundTo
import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.core.model.asString
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.presentation.R
import com.shifthackz.aisdv1.presentation.core.GenerationMviState
import com.shifthackz.aisdv1.presentation.theme.sliderColors
import com.shifthackz.aisdv1.presentation.utils.Constants
import com.shifthackz.aisdv1.presentation.utils.Constants.CFG_SCALE_RANGE_MAX
import com.shifthackz.aisdv1.presentation.utils.Constants.CFG_SCALE_RANGE_MIN
import com.shifthackz.aisdv1.presentation.utils.Constants.SAMPLING_STEPS_RANGE_MAX
import com.shifthackz.aisdv1.presentation.utils.Constants.SAMPLING_STEPS_RANGE_MIN
import com.shifthackz.aisdv1.presentation.utils.Constants.SUB_SEED_STRENGTH_MAX
import com.shifthackz.aisdv1.presentation.utils.Constants.SUB_SEED_STRENGTH_MIN
import kotlin.math.abs
import kotlin.math.roundToInt

enum class GenerationInputMode {
    AUTOMATIC1111,
    HORDE,
    LOCAL;

    companion object {
        fun fromSource(source: ServerSource) = when (source) {
            ServerSource.HORDE -> HORDE
            ServerSource.LOCAL -> LOCAL
            else -> AUTOMATIC1111
        }
    }
}

@Composable
fun GenerationInputForm(
    modifier: Modifier = Modifier,
    state: GenerationMviState,
    onShowAdvancedOptionsToggle: (Boolean) -> Unit = {},
    onPromptUpdated: (String) -> Unit = {},
    onNegativePromptUpdated: (String) -> Unit = {},
    onWidthUpdated: (String) -> Unit = {},
    onHeightUpdated: (String) -> Unit = {},
    onSamplingStepsUpdated: (Int) -> Unit = {},
    onCfgScaleUpdated: (Float) -> Unit = {},
    onRestoreFacesUpdated: (Boolean) -> Unit = {},
    onSeedUpdated: (String) -> Unit = {},
    onSubSeedUpdated: (String) -> Unit = {},
    onSubSeedStrengthUpdated: (Float) -> Unit = {},
    onSamplerUpdated: (String) -> Unit = {},
    widthValidationError: UiText? = null,
    heightValidationError: UiText? = null,
    afterSlidersSection: @Composable () -> Unit = {},
) {
    Column(modifier = modifier) {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            value = state.prompt,
            onValueChange = onPromptUpdated,
            label = { Text(stringResource(id = R.string.hint_prompt)) },
        )
        if (state.mode != GenerationInputMode.HORDE) TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            value = state.negativePrompt,
            onValueChange = onNegativePromptUpdated,
            label = { Text(stringResource(id = R.string.hint_prompt_negative)) },
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
        ) {
            val localModifier = Modifier.weight(1f)


            if (state.mode == GenerationInputMode.HORDE || state.mode == GenerationInputMode.LOCAL) {
                DropdownTextField(
                    modifier = localModifier.padding(end = 4.dp),
                    label = R.string.width.asUiText(),
                    value = state.width,
                    items = Constants.sizes,
                    onItemSelected = onWidthUpdated,
                )
                DropdownTextField(
                    modifier = localModifier.padding(start = 4.dp),
                    label = R.string.height.asUiText(),
                    value = state.height,
                    items = Constants.sizes,
                    onItemSelected = onHeightUpdated,
                )
            } else {
                TextField(
                    modifier = localModifier.padding(end = 4.dp),
                    value = state.width,
                    onValueChange = { value ->
                        if (value.length <= 4) {
                            value
                                .filter { it.isDigit() }
                                .let(onWidthUpdated)
                        }
                    },
                    isError = widthValidationError != null,
                    supportingText = { widthValidationError?.let { Text(it.asString(), color = MaterialTheme.colorScheme.error) } },
                    label = { Text(stringResource(id = R.string.width)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )
                TextField(
                    modifier = localModifier.padding(start = 4.dp),
                    value = state.height,
                    onValueChange = { value ->
                        if (value.length <= 4) {
                            value
                                .filter { it.isDigit() }
                                .let(onHeightUpdated)
                        }
                    },
                    isError = heightValidationError != null,
                    supportingText = { heightValidationError?.let { Text(it.asString(), color = MaterialTheme.colorScheme.error) } },
                    label = { Text(stringResource(id = R.string.height)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )
            }
        }
        if (state.advancedToggleButtonVisible && state.mode != GenerationInputMode.LOCAL) {
            TextButton(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = { onShowAdvancedOptionsToggle(!state.advancedOptionsVisible) },
            ) {
                Icon(
                    imageVector = if (state.advancedOptionsVisible) Icons.Default.ArrowDropUp
                    else Icons.Default.ArrowDropDown,
                    contentDescription = null,
                )
                Text(
                    text = stringResource(
                        id = if (state.advancedOptionsVisible) R.string.action_options_hide
                        else R.string.action_options_show
                    )
                )
            }
        }
        if (state.mode != GenerationInputMode.LOCAL) {
            AnimatedVisibility(visible = state.advancedOptionsVisible) {
                Column {
                    if (state.mode == GenerationInputMode.AUTOMATIC1111) DropdownTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        label = R.string.hint_sampler.asUiText(),
                        value = state.selectedSampler,
                        items = state.availableSamplers,
                        onItemSelected = onSamplerUpdated,
                    )
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        value = state.seed,
                        onValueChange = { value ->
                            value
                                .filter { it.isDigit() }
                                .let(onSeedUpdated)
                        },
                        label = { Text(stringResource(id = R.string.hint_seed)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    )
                    if (state.mode == GenerationInputMode.AUTOMATIC1111) TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        value = state.subSeed,
                        onValueChange = { value ->
                            value
                                .filter { it.isDigit() }
                                .let(onSubSeedUpdated)
                        },
                        label = { Text(stringResource(id = R.string.hint_sub_seed)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    )
                    Text(
                        modifier = Modifier.padding(top = 8.dp),
                        text = stringResource(
                            id = R.string.hint_sub_seed_strength,
                            "${state.subSeedStrength.roundTo(2)}",
                        ),
                    )
                    Slider(
                        value = state.subSeedStrength,
                        valueRange = SUB_SEED_STRENGTH_MIN..SUB_SEED_STRENGTH_MAX,
                        colors = sliderColors,
                        onValueChange = {
                            onSubSeedStrengthUpdated(it.roundTo(2))
                        },
                    )
                    Text(
                        modifier = Modifier.padding(top = 8.dp),
                        text = stringResource(
                            id = R.string.hint_sampling_steps,
                            "${state.samplingSteps}"
                        ),
                    )
                    Slider(
                        value = state.samplingSteps * 1f,
                        valueRange = (SAMPLING_STEPS_RANGE_MIN * 1f)..(SAMPLING_STEPS_RANGE_MAX * 1f),
                        steps = abs(SAMPLING_STEPS_RANGE_MAX - SAMPLING_STEPS_RANGE_MIN) - 1,
                        colors = sliderColors,
                        onValueChange = {
                            onSamplingStepsUpdated(it.roundToInt())
                        },
                    )
                    Text(
                        modifier = Modifier.padding(top = 8.dp),
                        text = stringResource(id = R.string.hint_cfg_scale, "${state.cfgScale}"),
                    )
                    Slider(
                        value = state.cfgScale,
                        valueRange = (CFG_SCALE_RANGE_MIN * 1f)..(CFG_SCALE_RANGE_MAX * 1f),
                        steps = abs(CFG_SCALE_RANGE_MAX - CFG_SCALE_RANGE_MIN) * 2 - 1,
                        colors = sliderColors,
                        onValueChange = {
                            onCfgScaleUpdated(it.roundTo(1))
                        },
                    )
                    afterSlidersSection()
                    if (state.mode == GenerationInputMode.AUTOMATIC1111) Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = state.restoreFaces,
                            onCheckedChange = onRestoreFacesUpdated,
                        )
                        Text(
                            text = stringResource(id = R.string.hint_restore_faces),
                        )
                    }
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun GenerationInputFormAutomaticPreview() {
    GenerationInputForm(
        state = object : GenerationMviState() {
            override val mode: GenerationInputMode = GenerationInputMode.AUTOMATIC1111
            override val advancedToggleButtonVisible: Boolean = true
            override val advancedOptionsVisible: Boolean = false
            override val prompt: String = "Opel Astra H OPC"
            override val negativePrompt: String = "Bad roads"
            override val width: String = "512"
            override val height: String = "512"
            override val samplingSteps: Int = 20
            override val cfgScale: Float = 11.5f
            override val restoreFaces: Boolean = true
            override val seed: String = "-1"
            override val subSeed: String = "-1"
            override val subSeedStrength: Float = 0f
            override val selectedSampler: String = "Euler a"
            override val availableSamplers: List<String> = listOf("Euler a")
            override val widthValidationError: UiText? = null
            override val heightValidationError: UiText? = null
            override val generateButtonEnabled: Boolean = true
        },
    )
}

@Composable
@Preview(showBackground = true)
private fun GenerationInputFormAutomaticWithOptionsPreview() {
    GenerationInputForm(
        state = object : GenerationMviState() {
            override val mode: GenerationInputMode = GenerationInputMode.AUTOMATIC1111
            override val advancedToggleButtonVisible: Boolean = false
            override val advancedOptionsVisible: Boolean = true
            override val prompt: String = "Opel Astra H OPC"
            override val negativePrompt: String = "Bad roads"
            override val width: String = "512"
            override val height: String = "512"
            override val samplingSteps: Int = 20
            override val cfgScale: Float = 11.5f
            override val restoreFaces: Boolean = true
            override val seed: String = "-1"
            override val subSeed: String = "-1"
            override val subSeedStrength: Float = 0f
            override val selectedSampler: String = "Euler a"
            override val availableSamplers: List<String> = listOf("Euler a")
            override val widthValidationError: UiText? = null
            override val heightValidationError: UiText? = null
            override val generateButtonEnabled: Boolean = true
        },
    )
}

@Composable
@Preview(showBackground = true)
private fun GenerationInputFormHordePreview() {
    GenerationInputForm(
        state = object : GenerationMviState() {
            override val mode: GenerationInputMode = GenerationInputMode.HORDE
            override val advancedToggleButtonVisible: Boolean = true
            override val advancedOptionsVisible: Boolean = false
            override val prompt: String = "Opel Astra H OPC"
            override val negativePrompt: String = "Bad roads"
            override val width: String = "512"
            override val height: String = "512"
            override val samplingSteps: Int = 20
            override val cfgScale: Float = 11.5f
            override val restoreFaces: Boolean = true
            override val seed: String = "-1"
            override val subSeed: String = "-1"
            override val subSeedStrength: Float = 0f
            override val selectedSampler: String = "Euler a"
            override val availableSamplers: List<String> = listOf("Euler a")
            override val widthValidationError: UiText? = null
            override val heightValidationError: UiText? = null
            override val generateButtonEnabled: Boolean = true
        },
    )
}

@Composable
@Preview(showBackground = true)
private fun GenerationInputFormHordeWithOptionsPreview() {
    GenerationInputForm(
        state = object : GenerationMviState() {
            override val mode: GenerationInputMode = GenerationInputMode.HORDE
            override val advancedToggleButtonVisible: Boolean = false
            override val advancedOptionsVisible: Boolean = true
            override val prompt: String = "Opel Astra H OPC"
            override val negativePrompt: String = "Bad roads"
            override val width: String = "512"
            override val height: String = "512"
            override val samplingSteps: Int = 20
            override val cfgScale: Float = 11.5f
            override val restoreFaces: Boolean = true
            override val seed: String = "-1"
            override val subSeed: String = "-1"
            override val subSeedStrength: Float = 0f
            override val selectedSampler: String = "Euler a"
            override val availableSamplers: List<String> = listOf("Euler a")
            override val widthValidationError: UiText? = null
            override val heightValidationError: UiText? = null
            override val generateButtonEnabled: Boolean = true
        },
    )
}
