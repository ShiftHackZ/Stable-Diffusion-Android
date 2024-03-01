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
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.core.common.math.roundTo
import com.shifthackz.aisdv1.core.model.asString
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.domain.entity.OpenAiModel
import com.shifthackz.aisdv1.domain.entity.OpenAiQuality
import com.shifthackz.aisdv1.domain.entity.OpenAiSize
import com.shifthackz.aisdv1.domain.entity.OpenAiStyle
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.presentation.R
import com.shifthackz.aisdv1.presentation.core.GenerationMviIntent
import com.shifthackz.aisdv1.presentation.core.GenerationMviState
import com.shifthackz.aisdv1.presentation.theme.sliderColors
import com.shifthackz.aisdv1.presentation.utils.Constants
import com.shifthackz.aisdv1.presentation.utils.Constants.BATCH_RANGE_MAX
import com.shifthackz.aisdv1.presentation.utils.Constants.BATCH_RANGE_MIN
import com.shifthackz.aisdv1.presentation.utils.Constants.CFG_SCALE_RANGE_MAX
import com.shifthackz.aisdv1.presentation.utils.Constants.CFG_SCALE_RANGE_MIN
import com.shifthackz.aisdv1.presentation.utils.Constants.SAMPLING_STEPS_RANGE_MAX
import com.shifthackz.aisdv1.presentation.utils.Constants.SAMPLING_STEPS_RANGE_MIN
import com.shifthackz.aisdv1.presentation.utils.Constants.SUB_SEED_STRENGTH_MAX
import com.shifthackz.aisdv1.presentation.utils.Constants.SUB_SEED_STRENGTH_MIN
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.random.Random

@Composable
fun GenerationInputForm(
    modifier: Modifier = Modifier,
    state: GenerationMviState,
    processIntent: (GenerationMviIntent) -> Unit = {},
    afterSlidersSection: @Composable () -> Unit = {},
) {
    @Composable
    fun batchComponent() {
        Text(
            modifier = Modifier.padding(top = 8.dp),
            text = stringResource(
                id = R.string.hint_batch,
                "${state.batchCount}",
            ),
        )
        Slider(
            value = state.batchCount * 1f,
            valueRange = (BATCH_RANGE_MIN * 1f)..(BATCH_RANGE_MAX * 1f),
            steps = abs(BATCH_RANGE_MIN - BATCH_RANGE_MAX) - 1,
            colors = sliderColors,
            onValueChange = { processIntent(GenerationMviIntent.Update.Batch(it.roundToInt())) },
        )
    }
    Column(modifier = modifier) {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            value = state.prompt,
            onValueChange = { processIntent(GenerationMviIntent.Update.Prompt(it)) },
            label = { Text(stringResource(id = R.string.hint_prompt)) },
        )

        // Horde does not support "negative prompt"
        when (state.mode) {
            ServerSource.AUTOMATIC1111,
            ServerSource.HUGGING_FACE,
            ServerSource.LOCAL -> TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                value = state.negativePrompt,
                onValueChange = { processIntent(GenerationMviIntent.Update.NegativePrompt(it))  },
                label = { Text(stringResource(id = R.string.hint_prompt_negative)) },
            )

            else -> Unit
        }

        if (state.mode == ServerSource.OPEN_AI) {
            DropdownTextField(
                modifier = Modifier.padding(top = 8.dp),
                label = R.string.hint_model_open_ai.asUiText(),
                value = state.openAiModel,
                items = OpenAiModel.entries,
                onItemSelected = { processIntent(GenerationMviIntent.Update.OpenAi.Model(it)) },
            )
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
                ServerSource.LOCAL -> {
                    DropdownTextField(
                        modifier = localModifier.padding(end = 4.dp),
                        label = R.string.width.asUiText(),
                        value = state.width,
                        items = Constants.sizes,
                        onItemSelected = { processIntent(GenerationMviIntent.Update.Size.Width(it)) },
                    )
                    DropdownTextField(
                        modifier = localModifier.padding(start = 4.dp),
                        label = R.string.height.asUiText(),
                        value = state.height,
                        items = Constants.sizes,
                        onItemSelected = { processIntent(GenerationMviIntent.Update.Size.Height(it)) },
                    )
                }

                ServerSource.AUTOMATIC1111,
                ServerSource.HUGGING_FACE -> {
                    TextField(
                        modifier = localModifier.padding(end = 4.dp),
                        value = state.width,
                        onValueChange = { value ->
                            if (value.length <= 4) {
                                value
                                    .filter { it.isDigit() }
                                    .let(GenerationMviIntent.Update.Size::Width)
                                    .let(processIntent::invoke)
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
                                    .let(GenerationMviIntent.Update.Size::Height)
                                    .let(processIntent::invoke)
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
                        label = { Text(stringResource(id = R.string.height)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    )
                }

                ServerSource.OPEN_AI -> {
                    DropdownTextField(
                        label = R.string.hint_image_size.asUiText(),
                        value = state.openAiSize,
                        items = OpenAiSize.entries.filter {
                            it.supportedModels.contains(state.openAiModel)
                        },
                        onItemSelected = { processIntent(GenerationMviIntent.Update.OpenAi.Size(it)) },
                        displayDelegate = { it.key.asUiText() },
                    )
                }
            }
        }

        if (state.mode == ServerSource.OPEN_AI) {
            if (state.openAiModel == OpenAiModel.DALL_E_3) {
                DropdownTextField(
                    modifier = Modifier.padding(top = 8.dp),
                    label = R.string.hint_quality.asUiText(),
                    value = state.openAiQuality,
                    items = OpenAiQuality.entries,
                    onItemSelected = { processIntent(GenerationMviIntent.Update.OpenAi.Quality(it)) },
                )
                DropdownTextField(
                    modifier = Modifier.padding(top = 8.dp),
                    label = R.string.hint_style.asUiText(),
                    value = state.openAiStyle,
                    items = OpenAiStyle.entries,
                    onItemSelected = { processIntent(GenerationMviIntent.Update.OpenAi.Style(it)) },
                )
            }
            batchComponent()
        }

        if (state.advancedToggleButtonVisible && state.mode != ServerSource.OPEN_AI) {
            TextButton(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = {
                    processIntent(
                        GenerationMviIntent.SetAdvancedOptionsVisibility(!state.advancedOptionsVisible)
                    )
                },
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

        AnimatedVisibility(
            visible = state.advancedOptionsVisible && state.mode != ServerSource.OPEN_AI,
        ) {
            Column {
                // Sampler selection only supported for A1111
                if (state.mode == ServerSource.AUTOMATIC1111) {
                    DropdownTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        label = R.string.hint_sampler.asUiText(),
                        value = state.selectedSampler,
                        items = state.availableSamplers,
                        onItemSelected = { processIntent(GenerationMviIntent.Update.Sampler(it)) },
                    )
                }
                // Seed is not available for Hugging Face
                if (state.mode != ServerSource.OPEN_AI) {
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        value = state.seed,
                        onValueChange = { value ->
                            value
                                .filter { it.isDigit() }
                                .let(GenerationMviIntent.Update::Seed)
                                .let(processIntent::invoke)
                        },
                        label = { Text(stringResource(id = R.string.hint_seed)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        trailingIcon = {
                            IconButton(onClick = {
                                processIntent(GenerationMviIntent.Update.Seed("${Random.nextLong()}"))
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Casino,
                                    contentDescription = "Random",
                                )
                            }
                        },
                    )
                }
                // NSFW flag specifically for Horde API
                if (state.mode == ServerSource.HORDE) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Switch(
                            checked = state.nsfw,
                            onCheckedChange = { processIntent(GenerationMviIntent.Update.Nsfw(it)) },
                        )
                        Text(
                            modifier = Modifier.padding(horizontal = 8.dp),
                            text = stringResource(id = R.string.hint_nsfw),
                        )
                    }
                }
                // Variation seed only supported for A1111
                if (state.mode == ServerSource.AUTOMATIC1111) {
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        value = state.subSeed,
                        onValueChange = { value ->
                            value
                                .filter { it.isDigit() }
                                .let(GenerationMviIntent.Update::SubSeed)
                                .let(processIntent::invoke)
                        },
                        label = { Text(stringResource(id = R.string.hint_sub_seed)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        trailingIcon = {
                            IconButton(onClick = {
                                processIntent(GenerationMviIntent.Update.SubSeed("${Random.nextLong()}"))
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Casino,
                                    contentDescription = "Random",
                                )
                            }
                        },
                    )
                }
                // Sub-seed strength is not available for Local Diffusion
                when (state.mode) {
                    ServerSource.AUTOMATIC1111,
                    ServerSource.HORDE -> {
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
                                processIntent(GenerationMviIntent.Update.SubSeedStrength(it))
                            },
                        )
                    }

                    else -> Unit
                }

                if (state.mode != ServerSource.OPEN_AI) {
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
                            processIntent(GenerationMviIntent.Update.SamplingSteps(it.roundToInt()))
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
                            processIntent(GenerationMviIntent.Update.CfgScale(it))
                        },
                    )
                }

                when (state.mode) {
                    ServerSource.AUTOMATIC1111,
                    ServerSource.HORDE -> afterSlidersSection()

                    else -> Unit
                }

                // Batch is not available for Local Diffusion
                if (state.mode != ServerSource.LOCAL) { batchComponent() }
                //Restore faces available only for A1111
                if (state.mode == ServerSource.AUTOMATIC1111) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Switch(
                            checked = state.restoreFaces,
                            onCheckedChange = {
                                processIntent(GenerationMviIntent.Update.RestoreFaces(it))
                            },
                        )
                        Text(
                            modifier = Modifier.padding(horizontal = 8.dp),
                            text = stringResource(id = R.string.hint_restore_faces),
                        )
                    }
                }
            }
        }
    }
}
