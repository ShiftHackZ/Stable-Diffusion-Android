package com.shifthackz.aisdv1.presentation.screen.txt2img

import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.core.validation.ValidationResult
import com.shifthackz.aisdv1.core.validation.dimension.DimensionValidator
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import com.shifthackz.aisdv1.presentation.R
import com.shifthackz.aisdv1.presentation.core.GenerationMviState
import com.shifthackz.aisdv1.presentation.model.Modal

data class TextToImageState(
    override val screenModal: Modal = Modal.None,
    override val mode: ServerSource = ServerSource.AUTOMATIC1111,
    override val advancedToggleButtonVisible: Boolean = true,
    override val advancedOptionsVisible: Boolean = false,
    override val prompt: String = "",
    override val negativePrompt: String = "",
    override val width: String = 512.toString(),
    override val height: String = 512.toString(),
    override val samplingSteps: Int = 20,
    override val cfgScale: Float = 7f,
    override val restoreFaces: Boolean = false,
    override val seed: String = "",
    override val subSeed: String = "",
    override val subSeedStrength: Float = 0f,
    override val selectedSampler: String = "",
    override val availableSamplers: List<String> = emptyList(),
    override val widthValidationError: UiText? = null,
    override val heightValidationError: UiText? = null,
    override val nsfw: Boolean = false,
    override val batchCount: Int = 1,
    override val generateButtonEnabled: Boolean = true,
) : GenerationMviState() {

    override fun copyState(
        screenModal: Modal,
        mode: ServerSource,
        advancedToggleButtonVisible: Boolean,
        advancedOptionsVisible: Boolean,
        prompt: String,
        negativePrompt: String,
        width: String,
        height: String,
        samplingSteps: Int,
        cfgScale: Float,
        restoreFaces: Boolean,
        seed: String,
        subSeed: String,
        subSeedStrength: Float,
        selectedSampler: String,
        availableSamplers: List<String>,
        widthValidationError: UiText?,
        heightValidationError: UiText?,
        nsfw: Boolean,
        batchCount: Int,
        generateButtonEnabled: Boolean
    ): GenerationMviState = copy(
        screenModal = screenModal,
        mode = mode,
        advancedToggleButtonVisible = advancedToggleButtonVisible,
        advancedOptionsVisible = advancedOptionsVisible,
        prompt = prompt,
        negativePrompt = negativePrompt,
        width = width,
        height = height,
        samplingSteps = samplingSteps,
        cfgScale = cfgScale,
        restoreFaces = restoreFaces,
        seed = seed,
        subSeed = subSeed,
        subSeedStrength = subSeedStrength,
        selectedSampler = selectedSampler,
        availableSamplers = availableSamplers,
        widthValidationError = widthValidationError,
        heightValidationError = heightValidationError,
        nsfw = nsfw,
        batchCount = batchCount,
        generateButtonEnabled = generateButtonEnabled,
    )
}

fun TextToImageState.mapToPayload(): TextToImagePayload = with(this) {
    TextToImagePayload(
        prompt = prompt.trim(),
        negativePrompt = negativePrompt.trim(),
        samplingSteps = samplingSteps,
        cfgScale = cfgScale,
        width = width.toIntOrNull() ?: 64,
        height = height.toIntOrNull() ?: 64,
        restoreFaces = restoreFaces,
        seed = seed.trim(),
        subSeed = subSeed.trim(),
        subSeedStrength = subSeedStrength,
        sampler = selectedSampler,
        nsfw = if (mode == ServerSource.HORDE) nsfw else false,
        batchCount = if (mode == ServerSource.LOCAL) 1 else batchCount,
    )
}

fun ValidationResult<DimensionValidator.Error>.mapToUi(): UiText? {
    if (this.isValid) return null
    return when (validationError as DimensionValidator.Error) {
        DimensionValidator.Error.Empty -> R.string.error_empty.asUiText()
        is DimensionValidator.Error.LessThanMinimum -> UiText.Resource(
            R.string.error_min_size,
            (validationError as DimensionValidator.Error.LessThanMinimum).min,
        )
        is DimensionValidator.Error.BiggerThanMaximum -> UiText.Resource(
            R.string.error_max_size,
            (validationError as DimensionValidator.Error.BiggerThanMaximum).max,
        )
        else -> R.string.error_invalid.asUiText()
    }
}
