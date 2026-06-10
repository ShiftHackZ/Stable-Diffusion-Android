package com.shifthackz.aisdv1.presentation.screen.img2img

import androidx.compose.runtime.Immutable
import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.core.mvi.MviState
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.ImageToImagePayload
import com.shifthackz.aisdv1.domain.entity.OpenAiModel
import com.shifthackz.aisdv1.domain.entity.OpenAiQuality
import com.shifthackz.aisdv1.domain.entity.OpenAiSize
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.entity.StabilityAiClipGuidance
import com.shifthackz.aisdv1.domain.entity.StabilityAiStylePreset
import com.shifthackz.aisdv1.presentation.model.GenerationModal
import com.shifthackz.aisdv1.presentation.model.PromptTagEditRequest
import com.shifthackz.aisdv1.presentation.widget.input.GenerationInputFormState

@Immutable
data class ImageToImageState(
    val loadingConfiguration: Boolean = true,
    val imageBase64: String = "",
    val denoisingStrength: Float = DEFAULT_DENOISING_STRENGTH,
    val pickingImage: Boolean = false,
    val generating: Boolean = false,
    val savingImage: Boolean = false,
    val sharingImage: Boolean = false,
    val inPaint: ImageInPaintState = ImageInPaintState(),
    val promptValidationError: String? = null,
    val error: String? = null,
    val message: String? = null,
    val screenModal: GenerationModal = GenerationModal.None,
    val results: List<AiGenerationResult> = emptyList(),
    val editTag: PromptTagEditRequest? = null,
    override val onBoardingDemo: Boolean = false,
    override val mode: ServerSource = ServerSource.AUTOMATIC1111,
    override val advancedToggleButtonVisible: Boolean = true,
    override val advancedOptionsVisible: Boolean = false,
    override val formPromptTaggedInput: Boolean = false,
    override val prompt: String = "",
    override val negativePrompt: String = "",
    override val width: String = DEFAULT_SIZE.toString(),
    override val height: String = DEFAULT_SIZE.toString(),
    override val samplingSteps: Int = 20,
    override val cfgScale: Float = 7f,
    override val restoreFaces: Boolean = false,
    override val seed: String = "",
    override val subSeed: String = "",
    override val subSeedStrength: Float = 0f,
    override val selectedSampler: String = "",
    override val availableSamplers: List<String> = emptyList(),
    override val selectedStylePreset: StabilityAiStylePreset = StabilityAiStylePreset.NONE,
    override val selectedClipGuidancePreset: StabilityAiClipGuidance = StabilityAiClipGuidance.NONE,
    override val openAiModel: OpenAiModel = OpenAiModel.default,
    override val openAiSize: OpenAiSize = OpenAiSize.W1024_H1024,
    override val openAiQuality: OpenAiQuality = OpenAiQuality.AUTO,
    override val widthValidationError: UiText? = null,
    override val heightValidationError: UiText? = null,
    override val nsfw: Boolean = false,
    override val batchCount: Int = 1,
) : MviState, GenerationInputFormState {

    val hasInputImage: Boolean
        get() = imageBase64.isNotBlank()

    val sourceSupportsImageToImage: Boolean
        get() = mode == ServerSource.AUTOMATIC1111 ||
            mode == ServerSource.SWARM_UI ||
            mode == ServerSource.HORDE ||
            mode == ServerSource.HUGGING_FACE ||
            mode == ServerSource.STABILITY_AI

    val hasValidationErrors: Boolean
        get() = promptValidationError != null ||
            widthValidationError != null ||
            heightValidationError != null ||
            error != null

    val canGenerate: Boolean
        get() = hasInputImage &&
            sourceSupportsImageToImage &&
            !hasValidationErrors &&
            !pickingImage &&
            !generating
}

internal fun ImageToImageState.mapToPayload(
    maskBase64: String? = null,
): ImageToImagePayload = ImageToImagePayload(
    base64Image = imageBase64,
    base64MaskImage = maskBase64.orEmpty(),
    denoisingStrength = denoisingStrength,
    prompt = prompt.trim(),
    negativePrompt = negativePrompt.trim(),
    samplingSteps = samplingSteps,
    cfgScale = cfgScale,
    width = width.toIntOrNull() ?: DEFAULT_SIZE,
    height = height.toIntOrNull() ?: DEFAULT_SIZE,
    restoreFaces = restoreFaces,
    seed = seed.trim(),
    subSeed = subSeed.trim(),
    subSeedStrength = subSeedStrength,
    sampler = selectedSampler,
    nsfw = if (mode == ServerSource.HORDE) nsfw else false,
    batchCount = batchCount,
    inPaintingMaskInvert = inPaint.maskMode.inverse,
    inPaintFullResPadding = inPaint.onlyMaskedPaddingPx,
    inPaintingFill = inPaint.maskContent.fill,
    inPaintFullRes = inPaint.area.fullRes,
    maskBlur = inPaint.maskBlur,
    stabilityAiClipGuidance = selectedClipGuidancePreset.takeIf { mode == ServerSource.STABILITY_AI },
    stabilityAiStylePreset = selectedStylePreset.takeIf { mode == ServerSource.STABILITY_AI },
)

internal const val DEFAULT_SIZE = 512
internal const val DEFAULT_DENOISING_STRENGTH = 0.75f
