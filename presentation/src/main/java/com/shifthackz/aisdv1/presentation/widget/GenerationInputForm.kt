@file:OptIn(ExperimentalMaterial3Api::class)

package com.shifthackz.aisdv1.presentation.widget

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.shifthackz.aisdv1.presentation.R
import com.shifthackz.aisdv1.presentation.theme.sliderColors
import com.shifthackz.aisdv1.presentation.utils.Constants.CFG_SCALE_RANGE_MAX
import com.shifthackz.aisdv1.presentation.utils.Constants.CFG_SCALE_RANGE_MIN
import com.shifthackz.aisdv1.presentation.utils.Constants.SAMPLING_STEPS_RANGE_MAX
import com.shifthackz.aisdv1.presentation.utils.Constants.SAMPLING_STEPS_RANGE_MIN
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun GenerationInputForm(
    modifier: Modifier = Modifier,
    prompt: String,
    negativePrompt: String,
    width: String,
    height: String,
    samplingSteps: Int,
    cfgScale: Float,
    restoreFaces: Boolean,
    seed: String,
    selectedSampler: String,
    availableSamplers: List<String>,
    onPromptUpdated: (String) -> Unit = {},
    onNegativePromptUpdated: (String) -> Unit = {},
    onWidthUpdated: (String) -> Unit = {},
    onHeightUpdated: (String) -> Unit = {},
    onSamplingStepsUpdated: (Int) -> Unit = {},
    onCfgScaleUpdated: (Float) -> Unit = {},
    onRestoreFacesUpdated: (Boolean) -> Unit = {},
    onSeedUpdated: (String) -> Unit = {},
    onSamplerUpdated: (String) -> Unit = {},
    widthValidationError: UiText? = null,
    heightValidationError: UiText? = null,
) {
    Column(modifier = modifier) {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            value = prompt,
            onValueChange = onPromptUpdated,
            label = { Text(stringResource(id = R.string.hint_prompt)) },
        )
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            value = negativePrompt,
            onValueChange = onNegativePromptUpdated,
            label = { Text(stringResource(id = R.string.hint_prompt_negative)) },
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
        ) {
            TextField(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 4.dp),
                value = width,
                onValueChange = { value ->
                    if (value.length <= 4) {
                        value
                            .filter { it.isDigit() }
                            .let(onWidthUpdated)
                    }
                },
                isError = widthValidationError != null,
                supportingText = { widthValidationError?.let { Text(it.asString()) } },
                label = { Text(stringResource(id = R.string.width)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
            TextField(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp),
                value = height,
                onValueChange = { value ->
                    if (value.length <= 4) {
                        value
                            .filter { it.isDigit() }
                            .let(onHeightUpdated)
                    }
                },
                isError = heightValidationError != null,
                supportingText = { heightValidationError?.let { Text(it.asString()) } },
                label = { Text(stringResource(id = R.string.height)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
        }
        DropdownTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            label = R.string.hint_sampler.asUiText(),
            value = selectedSampler,
            items = availableSamplers,
            onItemSelected = onSamplerUpdated,
        )
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            value = seed,
            onValueChange = { value ->
                value
                    .filter { it.isDigit() }
                    .let(onSeedUpdated)
            },
            label = { Text(stringResource(id = R.string.hint_seed)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        )
        Text(
            modifier = Modifier.padding(top = 8.dp),
            text = stringResource(id = R.string.hint_sampling_steps, "$samplingSteps"),
        )
        Slider(
            value = samplingSteps * 1f,
            valueRange = (SAMPLING_STEPS_RANGE_MIN * 1f)..(SAMPLING_STEPS_RANGE_MAX * 1f),
            steps = abs(SAMPLING_STEPS_RANGE_MAX - SAMPLING_STEPS_RANGE_MIN) - 1,
            colors = sliderColors,
            onValueChange = {
                onSamplingStepsUpdated(it.roundToInt())
            },
        )

        Text(
            modifier = Modifier.padding(top = 8.dp),
            text = stringResource(id = R.string.hint_cfg_scale, "$cfgScale"),
        )
        Slider(
            value = cfgScale,
            valueRange = (CFG_SCALE_RANGE_MIN * 1f)..(CFG_SCALE_RANGE_MAX * 1f),
            steps = abs(CFG_SCALE_RANGE_MAX - CFG_SCALE_RANGE_MIN) * 2 - 1,
            colors = sliderColors,
            onValueChange = {
                onCfgScaleUpdated(it.roundTo(1))
            },
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = restoreFaces,
                onCheckedChange = onRestoreFacesUpdated,
            )
            Text(
                text = stringResource(id = R.string.hint_restore_faces),
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun GenerationInputFormPreview() {
    GenerationInputForm(
        prompt = "Opel Astra H OPC",
        negativePrompt = "White background",
        width = "512",
        height = "512",
        samplingSteps = 55,
        cfgScale = 7f,
        restoreFaces = true,
        seed = "5598",
        selectedSampler = "Euler",
        availableSamplers = listOf("Euler")
    )
}
