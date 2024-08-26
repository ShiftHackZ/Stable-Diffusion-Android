package com.shifthackz.aisdv1.presentation.screen.txt2img

import androidx.compose.runtime.Immutable
import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.core.validation.ValidationResult
import com.shifthackz.aisdv1.core.validation.dimension.DimensionValidator
import com.shifthackz.aisdv1.domain.entity.OpenAiModel
import com.shifthackz.aisdv1.domain.entity.OpenAiQuality
import com.shifthackz.aisdv1.domain.entity.OpenAiSize
import com.shifthackz.aisdv1.domain.entity.OpenAiStyle
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.entity.StabilityAiClipGuidance
import com.shifthackz.aisdv1.domain.entity.StabilityAiStylePreset
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import com.shifthackz.aisdv1.presentation.core.GenerationMviState
import com.shifthackz.aisdv1.presentation.model.Modal
import com.shifthackz.aisdv1.core.localization.R as LocalizationR

@Immutable
data class TextToImageState(
    override val onBoardingDemo: Boolean = false,
    override val screenModal: Modal = Modal.None,
    override val mode: ServerSource = ServerSource.AUTOMATIC1111,
    override val advancedToggleButtonVisible: Boolean = true,
    override val advancedOptionsVisible: Boolean = false,
    override val formPromptTaggedInput: Boolean = false,
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
    override val selectedStylePreset: StabilityAiStylePreset = StabilityAiStylePreset.NONE,
    override val selectedClipGuidancePreset: StabilityAiClipGuidance = StabilityAiClipGuidance.NONE,
    override val openAiModel: OpenAiModel = OpenAiModel.DALL_E_2,
    override val openAiSize: OpenAiSize = OpenAiSize.W1024_H1024,
    override val openAiQuality: OpenAiQuality = OpenAiQuality.STANDARD,
    override val openAiStyle: OpenAiStyle = OpenAiStyle.VIVID,
    override val availableSamplers: List<String> = emptyList(),
    override val widthValidationError: UiText? = null,
    override val heightValidationError: UiText? = null,
    override val nsfw: Boolean = false,
    override val batchCount: Int = 1,
    override val generateButtonEnabled: Boolean = true,
) : GenerationMviState() {

    override fun copyState(
        onBoardingDemo: Boolean,
        screenModal: Modal,
        mode: ServerSource,
        advancedToggleButtonVisible: Boolean,
        advancedOptionsVisible: Boolean,
        formPromptTaggedInput: Boolean,
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
        selectedStylePreset: StabilityAiStylePreset,
        selectedClipGuidancePreset: StabilityAiClipGuidance,
        openAiModel: OpenAiModel,
        openAiSize: OpenAiSize,
        openAiQuality: OpenAiQuality,
        openAiStyle: OpenAiStyle,
        widthValidationError: UiText?,
        heightValidationError: UiText?,
        nsfw: Boolean,
        batchCount: Int,
        generateButtonEnabled: Boolean
    ): GenerationMviState = copy(
        onBoardingDemo = onBoardingDemo,
        screenModal = screenModal,
        mode = mode,
        advancedToggleButtonVisible = advancedToggleButtonVisible,
        advancedOptionsVisible = advancedOptionsVisible,
        formPromptTaggedInput = formPromptTaggedInput,
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
        selectedStylePreset = selectedStylePreset,
        selectedClipGuidancePreset = selectedClipGuidancePreset,
        openAiModel = openAiModel,
        openAiSize = openAiSize,
        openAiQuality = openAiQuality,
        openAiStyle = openAiStyle,
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
        width = when (mode) {
            ServerSource.OPEN_AI -> openAiSize.width
            else -> width.toIntOrNull() ?: 64
        },
        height = when (mode) {
            ServerSource.OPEN_AI -> openAiSize.height
            else -> height.toIntOrNull() ?: 64
        },
        restoreFaces = restoreFaces,
        seed = seed.trim(),
        subSeed = subSeed.trim(),
        subSeedStrength = subSeedStrength,
        sampler = selectedSampler,
        nsfw = if (mode == ServerSource.HORDE) nsfw else false,
        batchCount = if (mode == ServerSource.LOCAL_MICROSOFT_ONNX) 1 else batchCount,
        style = openAiStyle.key.takeIf {
            mode == ServerSource.OPEN_AI && openAiModel == OpenAiModel.DALL_E_3
        },
        quality = openAiQuality.key.takeIf {
            mode == ServerSource.OPEN_AI && openAiModel == OpenAiModel.DALL_E_3
        },
        openAiModel = openAiModel.takeIf { mode == ServerSource.OPEN_AI },
        stabilityAiClipGuidance = selectedClipGuidancePreset.takeIf { mode == ServerSource.STABILITY_AI },
        stabilityAiStylePreset = selectedStylePreset.takeIf { mode == ServerSource.STABILITY_AI },
    )
}

fun ValidationResult<DimensionValidator.Error>.mapToUi(): UiText? {
    if (this.isValid) return null
    return when (validationError as DimensionValidator.Error) {
        DimensionValidator.Error.Empty -> LocalizationR.string.error_empty.asUiText()
        is DimensionValidator.Error.LessThanMinimum -> UiText.Resource(
            LocalizationR.string.error_min_size,
            (validationError as DimensionValidator.Error.LessThanMinimum).min,
        )
        is DimensionValidator.Error.BiggerThanMaximum -> UiText.Resource(
            LocalizationR.string.error_max_size,
            (validationError as DimensionValidator.Error.BiggerThanMaximum).max,
        )
        else -> LocalizationR.string.error_invalid.asUiText()
    }
}
