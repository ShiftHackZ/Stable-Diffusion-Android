package com.shifthackz.aisdv1.presentation.widget.input

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.core.common.math.roundTo
import com.shifthackz.aisdv1.core.localization.Localization
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.domain.entity.ADetailerConfig
import com.shifthackz.aisdv1.domain.entity.HiresConfig
import com.shifthackz.aisdv1.domain.entity.Scheduler
import com.shifthackz.aisdv1.presentation.theme.sliderColors
import com.shifthackz.aisdv1.presentation.widget.input.GenerationInputFormConstants.ADETAILER_CONFIDENCE_MAX
import com.shifthackz.aisdv1.presentation.widget.input.GenerationInputFormConstants.ADETAILER_CONFIDENCE_MIN
import com.shifthackz.aisdv1.presentation.widget.input.GenerationInputFormConstants.DENOISING_STRENGTH_MAX
import com.shifthackz.aisdv1.presentation.widget.input.GenerationInputFormConstants.DENOISING_STRENGTH_MIN
import com.shifthackz.aisdv1.presentation.widget.input.GenerationInputFormConstants.HIRES_SCALE_MAX
import com.shifthackz.aisdv1.presentation.widget.input.GenerationInputFormConstants.HIRES_SCALE_MIN
import com.shifthackz.aisdv1.presentation.widget.input.GenerationInputFormConstants.HIRES_STEPS_MAX
import com.shifthackz.aisdv1.presentation.widget.input.GenerationInputFormConstants.HIRES_STEPS_MIN
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * Renders A1111-only generation options in the SDAI presentation layer.
 *
 * @param state state rendered or processed by the component.
 * @param isImg2Img is img2 img value consumed by the API.
 * @param onEvent callback invoked by the component.
 * @author Dmitriy Moroz
 */
@Composable
internal fun GenerationInputA1111Options(
    state: GenerationInputFormState,
    isImg2Img: Boolean,
    onEvent: (GenerationInputFormEvent) -> Unit,
) {
    SchedulerSection(
        config = state.selectedScheduler,
        onConfigChange = { onEvent(GenerationInputFormEvent.UpdateScheduler(it)) },
    )
    if (!isImg2Img && state.availableForgeModules.isNotEmpty()) {
        MultiSelectDropdownField(
            modifier = Modifier.padding(top = 8.dp),
            label = Localization.string("hint_forge_modules").asUiText(),
            selectedItems = state.selectedForgeModules,
            availableItems = state.availableForgeModules,
            onSelectionChanged = { onEvent(GenerationInputFormEvent.UpdateForgeModules(it)) },
            displayDelegate = { module -> module.name.ifBlank { module.path }.asUiText() },
        )
    }
    if (!isImg2Img) {
        HiresSection(
            modifier = Modifier.padding(top = 8.dp),
            config = state.hires,
            onConfigChange = { onEvent(GenerationInputFormEvent.UpdateHiresConfig(it)) },
        )
    }
    ADetailerSection(
        modifier = Modifier.padding(top = 8.dp),
        config = state.aDetailer,
        available = state.aDetailerAvailable,
        refreshing = state.aDetailerRefreshing,
        onConfigChange = { onEvent(GenerationInputFormEvent.UpdateADetailerConfig(it)) },
        onInstallClick = { onEvent(GenerationInputFormEvent.OpenADetailerInstallInstructions) },
        onRefreshClick = { onEvent(GenerationInputFormEvent.RefreshADetailerAvailability) },
    )
}

/**
 * Renders scheduler controls in the SDAI presentation layer.
 *
 * @param modifier Compose modifier applied to the rendered UI.
 * @param config config rendered or processed by the component.
 * @param onConfigChange callback invoked by the component.
 * @author Dmitriy Moroz
 */
@Composable
private fun SchedulerSection(
    modifier: Modifier = Modifier,
    config: Scheduler,
    onConfigChange: (Scheduler) -> Unit,
) {
    DropdownTextField(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        label = Localization.string("hint_scheduler").asUiText(),
        value = config,
        items = Scheduler.entries,
        onItemSelected = onConfigChange,
        displayDelegate = { it.displayName.asUiText() },
    )
}

/**
 * Renders Hires.fix controls in the SDAI presentation layer.
 *
 * @param modifier Compose modifier applied to the rendered UI.
 * @param config config rendered or processed by the component.
 * @param onConfigChange callback invoked by the component.
 * @author Dmitriy Moroz
 */
@Composable
private fun HiresSection(
    modifier: Modifier = Modifier,
    config: HiresConfig,
    onConfigChange: (HiresConfig) -> Unit,
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Switch(
                checked = config.enabled,
                onCheckedChange = { onConfigChange(config.copy(enabled = it)) },
            )
            Text(
                modifier = Modifier.padding(horizontal = 8.dp),
                text = Localization.string("hint_hires_enabled"),
            )
        }

        AnimatedVisibility(visible = config.enabled) {
            Column {
                DropdownTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    label = Localization.string("hint_hires_upscaler").asUiText(),
                    value = config.upscaler,
                    items = HiresConfig.AVAILABLE_UPSCALERS,
                    onItemSelected = { onConfigChange(config.copy(upscaler = it)) },
                    displayDelegate = { it.asUiText() },
                )
                Text(
                    modifier = Modifier.padding(top = 8.dp),
                    text = Localization.string(
                        "hint_hires_scale",
                        "${config.scale.roundTo(1)}",
                    ),
                )
                SliderTextInputField(
                    value = config.scale,
                    valueRange = HIRES_SCALE_MIN..HIRES_SCALE_MAX,
                    valueDiff = 0.1f,
                    fractionDigits = 1,
                    sliderColors = sliderColors,
                    onValueChange = { onConfigChange(config.copy(scale = it)) },
                )
                Text(
                    modifier = Modifier.padding(top = 8.dp),
                    text = Localization.string("hint_hires_steps", "${config.steps}"),
                )
                SliderTextInputField(
                    value = config.steps.toFloat(),
                    valueRange = HIRES_STEPS_MIN.toFloat()..HIRES_STEPS_MAX.toFloat(),
                    valueDiff = 1f,
                    fractionDigits = 0,
                    steps = abs(HIRES_STEPS_MAX - HIRES_STEPS_MIN) - 1,
                    sliderColors = sliderColors,
                    onValueChange = { onConfigChange(config.copy(steps = it.roundToInt())) },
                )
                Text(
                    modifier = Modifier.padding(top = 8.dp),
                    text = Localization.string(
                        "hint_hires_denoising",
                        "${config.denoisingStrength.roundTo(2)}",
                    ),
                )
                SliderTextInputField(
                    value = config.denoisingStrength,
                    valueRange = DENOISING_STRENGTH_MIN..DENOISING_STRENGTH_MAX,
                    valueDiff = 0.01f,
                    sliderColors = sliderColors,
                    onValueChange = { onConfigChange(config.copy(denoisingStrength = it)) },
                )
            }
        }
    }
}

/**
 * Renders ADetailer controls in the SDAI presentation layer.
 *
 * @param modifier Compose modifier applied to the rendered UI.
 * @param config config rendered or processed by the component.
 * @param onConfigChange callback invoked by the component.
 * @author Dmitriy Moroz
 */
@Composable
private fun ADetailerSection(
    modifier: Modifier = Modifier,
    config: ADetailerConfig,
    available: Boolean,
    refreshing: Boolean,
    onConfigChange: (ADetailerConfig) -> Unit,
    onInstallClick: () -> Unit,
    onRefreshClick: () -> Unit,
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Switch(
                checked = config.enabled,
                onCheckedChange = { onConfigChange(config.copy(enabled = it)) },
            )
            Text(
                modifier = Modifier.padding(horizontal = 8.dp),
                text = Localization.string("hint_adetailer_enabled"),
            )
        }

        AnimatedVisibility(visible = config.enabled) {
            if (!available) {
                ADetailerMissingContent(
                    refreshing = refreshing,
                    onInstallClick = onInstallClick,
                    onRefreshClick = onRefreshClick,
                )
            } else {
                Column {
                    DropdownTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        label = Localization.string("hint_adetailer_model").asUiText(),
                        value = config.model,
                        items = ADetailerConfig.AVAILABLE_MODELS,
                        onItemSelected = { onConfigChange(config.copy(model = it)) },
                        displayDelegate = { it.asUiText() },
                    )
                    Text(
                        modifier = Modifier.padding(top = 8.dp),
                        text = Localization.string(
                            "hint_adetailer_confidence",
                            "${config.confidence.roundTo(2)}",
                        ),
                    )
                    SliderTextInputField(
                        value = config.confidence,
                        valueRange = ADETAILER_CONFIDENCE_MIN..ADETAILER_CONFIDENCE_MAX,
                        valueDiff = 0.01f,
                        sliderColors = sliderColors,
                        onValueChange = { onConfigChange(config.copy(confidence = it)) },
                    )
                    Text(
                        modifier = Modifier.padding(top = 8.dp),
                        text = Localization.string(
                            "hint_adetailer_denoising",
                            "${config.denoisingStrength.roundTo(2)}",
                        ),
                    )
                    SliderTextInputField(
                        value = config.denoisingStrength,
                        valueRange = DENOISING_STRENGTH_MIN..DENOISING_STRENGTH_MAX,
                        valueDiff = 0.01f,
                        sliderColors = sliderColors,
                        onValueChange = { onConfigChange(config.copy(denoisingStrength = it)) },
                    )
                }
            }
        }
    }
}

/**
 * Renders ADetailer missing state in the SDAI presentation layer.
 *
 * @param refreshing refreshing value consumed by the API.
 * @param onInstallClick callback invoked by the component.
 * @param onRefreshClick callback invoked by the component.
 * @author Dmitriy Moroz
 */
@Composable
private fun ADetailerMissingContent(
    refreshing: Boolean,
    onInstallClick: () -> Unit,
    onRefreshClick: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            modifier = Modifier.padding(top = 8.dp),
            text = Localization.string("hint_adetailer_not_installed"),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
        ) {
            OutlinedButton(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 4.dp),
                onClick = onInstallClick,
            ) {
                Icon(imageVector = Icons.Default.Info, contentDescription = null)
                Text(
                    modifier = Modifier.padding(start = 8.dp),
                    text = Localization.string("action_install_instructions"),
                )
            }
            Button(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp),
                enabled = !refreshing,
                onClick = onRefreshClick,
            ) {
                if (refreshing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                    )
                } else {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
                }
                Text(
                    modifier = Modifier.padding(start = 8.dp),
                    text = Localization.string("action_refresh"),
                )
            }
        }
    }
}
