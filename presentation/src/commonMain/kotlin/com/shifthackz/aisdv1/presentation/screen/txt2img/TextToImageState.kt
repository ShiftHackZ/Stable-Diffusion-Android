package com.shifthackz.aisdv1.presentation.screen.txt2img

import androidx.compose.runtime.Immutable
import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.core.mvi.MviState
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.OpenAiModel
import com.shifthackz.aisdv1.domain.entity.OpenAiQuality
import com.shifthackz.aisdv1.domain.entity.OpenAiSize
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.entity.StabilityAiClipGuidance
import com.shifthackz.aisdv1.domain.entity.StabilityAiStylePreset
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import com.shifthackz.aisdv1.presentation.model.PromptTagEditRequest
import com.shifthackz.aisdv1.presentation.model.GenerationModal
import com.shifthackz.aisdv1.presentation.widget.input.GenerationInputFormState

@Immutable
data class TextToImageState(
    val loadingConfiguration: Boolean = true,
    val generating: Boolean = false,
    val savingImage: Boolean = false,
    val sharingImage: Boolean = false,
    val promptValidationError: UiText? = null,
    val error: UiText? = null,
    val message: UiText? = null,
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

    val localSourceSelected: Boolean
        get() = mode == ServerSource.LOCAL_MICROSOFT_ONNX ||
            mode == ServerSource.LOCAL_GOOGLE_MEDIA_PIPE

    val hasValidationErrors: Boolean
        get() = promptValidationError != null ||
            widthValidationError != null ||
            heightValidationError != null ||
            error != null
}

internal fun TextToImageState.mapToPayload(): TextToImagePayload = TextToImagePayload(
    prompt = prompt.trim(),
    negativePrompt = negativePrompt.trim(),
    samplingSteps = samplingSteps,
    cfgScale = cfgScale,
    width = when (mode) {
        ServerSource.OPEN_AI -> openAiSize.width
        else -> width.toIntOrNull() ?: DEFAULT_SIZE
    },
    height = when (mode) {
        ServerSource.OPEN_AI -> openAiSize.height
        else -> height.toIntOrNull() ?: DEFAULT_SIZE
    },
    restoreFaces = restoreFaces,
    seed = seed.trim(),
    subSeed = subSeed.trim(),
    subSeedStrength = subSeedStrength,
    sampler = selectedSampler,
    nsfw = if (mode == ServerSource.HORDE) nsfw else false,
    batchCount = if (mode == ServerSource.LOCAL_MICROSOFT_ONNX) 1 else batchCount,
    style = null,
    quality = openAiQuality.key.takeIf {
        mode == ServerSource.OPEN_AI && openAiQuality != OpenAiQuality.AUTO
    },
    openAiModel = openAiModel.takeIf { mode == ServerSource.OPEN_AI },
    stabilityAiClipGuidance = selectedClipGuidancePreset.takeIf { mode == ServerSource.STABILITY_AI },
    stabilityAiStylePreset = selectedStylePreset.takeIf { mode == ServerSource.STABILITY_AI },
)

internal const val DEFAULT_SIZE = 512
