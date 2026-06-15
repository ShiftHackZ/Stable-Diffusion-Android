package com.shifthackz.aisdv1.presentation.screen.txt2img

import androidx.compose.runtime.Immutable
import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.core.mvi.MviState
import com.shifthackz.aisdv1.domain.entity.ADetailerConfig
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.FalAiAcceleration
import com.shifthackz.aisdv1.domain.entity.FalAiImageSize
import com.shifthackz.aisdv1.domain.entity.FalAiModel
import com.shifthackz.aisdv1.domain.entity.ForgeModule
import com.shifthackz.aisdv1.domain.entity.HiresConfig
import com.shifthackz.aisdv1.domain.entity.OpenAiModel
import com.shifthackz.aisdv1.domain.entity.OpenAiQuality
import com.shifthackz.aisdv1.domain.entity.OpenAiSize
import com.shifthackz.aisdv1.domain.entity.Scheduler
import com.shifthackz.aisdv1.domain.entity.SdxlBackend
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.entity.StabilityAiClipGuidance
import com.shifthackz.aisdv1.domain.entity.StabilityAiStylePreset
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import com.shifthackz.aisdv1.presentation.model.GenerationModal
import com.shifthackz.aisdv1.presentation.model.PromptTagEditRequest
import com.shifthackz.aisdv1.presentation.widget.input.GenerationInputFormState

/**
 * Carries `TextToImageState` data through the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
@Immutable
data class TextToImageState(
    /**
     * Exposes the `loadingConfiguration` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val loadingConfiguration: Boolean = true,
    /**
     * Exposes the `generating` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val generating: Boolean = false,
    /**
     * Exposes the `savingImage` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val savingImage: Boolean = false,
    /**
     * Exposes the `sharingImage` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val sharingImage: Boolean = false,
    /**
     * Exposes the `promptValidationError` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val promptValidationError: UiText? = null,
    /**
     * Exposes the `error` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val error: UiText? = null,
    /**
     * Exposes the `message` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val message: UiText? = null,
    /**
     * Exposes the `screenModal` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val screenModal: GenerationModal = GenerationModal.None,
    /**
     * Exposes the `results` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val results: List<AiGenerationResult> = emptyList(),
    /**
     * Exposes the `editTag` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val editTag: PromptTagEditRequest? = null,
    /**
     * Exposes the `onBoardingDemo` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override val onBoardingDemo: Boolean = false,
    /**
     * Exposes the `mode` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override val mode: ServerSource = ServerSource.AUTOMATIC1111,
    /**
     * Exposes the `advancedToggleButtonVisible` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override val advancedToggleButtonVisible: Boolean = true,
    /**
     * Exposes the `advancedOptionsVisible` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override val advancedOptionsVisible: Boolean = false,
    /**
     * Exposes the `formPromptTaggedInput` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override val formPromptTaggedInput: Boolean = false,
    /**
     * Exposes the `prompt` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override val prompt: String = "",
    /**
     * Exposes the `negativePrompt` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override val negativePrompt: String = "",
    /**
     * Exposes the `width` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override val width: String = DEFAULT_SIZE.toString(),
    /**
     * Exposes the `height` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override val height: String = DEFAULT_SIZE.toString(),
    /**
     * Exposes the `samplingSteps` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override val samplingSteps: Int = 20,
    /**
     * Exposes the `cfgScale` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override val cfgScale: Float = 7f,
    /**
     * Exposes the `restoreFaces` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override val restoreFaces: Boolean = false,
    /**
     * Exposes the `seed` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override val seed: String = "",
    /**
     * Exposes the `subSeed` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override val subSeed: String = "",
    /**
     * Exposes the `subSeedStrength` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override val subSeedStrength: Float = 0f,
    /**
     * Exposes the `selectedSampler` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override val selectedSampler: String = "",
    /**
     * Exposes the `selectedScheduler` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override val selectedScheduler: Scheduler = Scheduler.AUTOMATIC,
    /**
     * Exposes the `availableForgeModules` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override val availableForgeModules: List<ForgeModule> = emptyList(),
    /**
     * Exposes the `selectedForgeModules` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override val selectedForgeModules: List<ForgeModule> = emptyList(),
    /**
     * Exposes the `availableSamplers` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override val availableSamplers: List<String> = emptyList(),
    /**
     * Exposes the `selectedStylePreset` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override val selectedStylePreset: StabilityAiStylePreset = StabilityAiStylePreset.NONE,
    /**
     * Exposes the `selectedClipGuidancePreset` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override val selectedClipGuidancePreset: StabilityAiClipGuidance = StabilityAiClipGuidance.NONE,
    /**
     * Exposes the `openAiModel` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override val openAiModel: OpenAiModel = OpenAiModel.default,
    /**
     * Exposes the `openAiSize` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override val openAiSize: OpenAiSize = OpenAiSize.W1024_H1024,
    /**
     * Exposes the `openAiQuality` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override val openAiQuality: OpenAiQuality = OpenAiQuality.AUTO,
    /**
     * Exposes the `falAiModel` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override val falAiModel: FalAiModel = FalAiModel.defaultTextToImage,
    /**
     * Exposes the `falAiImageSize` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override val falAiImageSize: FalAiImageSize = FalAiImageSize.default,
    /**
     * Exposes the `falAiAcceleration` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override val falAiAcceleration: FalAiAcceleration = FalAiAcceleration.default,
    /**
     * Exposes the `falAiSyncMode` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override val falAiSyncMode: Boolean = false,
    override val sdxlBackend: SdxlBackend = SdxlBackend.AUTO,
    override val arliAiModels: List<String> = emptyList(),
    override val arliAiModel: String = "",
    /**
     * Exposes the `widthValidationError` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override val widthValidationError: UiText? = null,
    /**
     * Exposes the `heightValidationError` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override val heightValidationError: UiText? = null,
    /**
     * Exposes the `nsfw` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override val nsfw: Boolean = false,
    /**
     * Exposes the `batchCount` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override val batchCount: Int = 1,
    /**
     * Exposes the `hires` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override val hires: HiresConfig = HiresConfig.DISABLED,
    /**
     * Exposes the `aDetailer` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override val aDetailer: ADetailerConfig = ADetailerConfig.DISABLED,
    /**
     * Exposes the `aDetailerAvailable` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override val aDetailerAvailable: Boolean = false,
    /**
     * Exposes the `aDetailerRefreshing` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override val aDetailerRefreshing: Boolean = false,
) : MviState, GenerationInputFormState {

    val localSourceSelected: Boolean
        get() = mode == ServerSource.LOCAL_MICROSOFT_ONNX ||
            mode == ServerSource.LOCAL_GOOGLE_MEDIA_PIPE ||
            mode == ServerSource.LOCAL_STABLE_DIFFUSION_CPP ||
            mode == ServerSource.LOCAL_APPLE_CORE_ML ||
            mode == ServerSource.LOCAL_APPLE_BONSAI

    val hasValidationErrors: Boolean
        get() = promptValidationError != null ||
            widthValidationError != null ||
            heightValidationError != null ||
            error != null
}

/**
 * Converts SDAI data with `mapToPayload`.
 *
 * @author Dmitriy Moroz
 */
internal fun TextToImageState.mapToPayload(): TextToImagePayload = TextToImagePayload(
    prompt = prompt.trim(),
    negativePrompt = negativePrompt.trim(),
    samplingSteps = samplingSteps,
    cfgScale = cfgScale,
    width = when (mode) {
        ServerSource.OPEN_AI -> openAiSize.width
        ServerSource.FAL_AI -> falAiImageSize.width
        ServerSource.LOCAL_APPLE_BONSAI -> width.toIntOrNull() ?: BONSAI_DEFAULT_SIZE
        else -> width.toIntOrNull() ?: DEFAULT_SIZE
    },
    height = when (mode) {
        ServerSource.OPEN_AI -> openAiSize.height
        ServerSource.FAL_AI -> falAiImageSize.height
        ServerSource.LOCAL_APPLE_BONSAI -> height.toIntOrNull() ?: BONSAI_DEFAULT_SIZE
        else -> height.toIntOrNull() ?: DEFAULT_SIZE
    },
    restoreFaces = restoreFaces,
    seed = seed.trim(),
    subSeed = subSeed.trim(),
    subSeedStrength = subSeedStrength,
    sampler = selectedSampler,
    scheduler = selectedScheduler.takeIf {
        mode == ServerSource.AUTOMATIC1111
    } ?: Scheduler.AUTOMATIC,
    nsfw = if (
        mode == ServerSource.HORDE ||
        mode == ServerSource.LOCAL_APPLE_CORE_ML ||
        mode == ServerSource.LOCAL_APPLE_BONSAI ||
        mode == ServerSource.FAL_AI
    ) nsfw else false,
    batchCount = if (
        mode == ServerSource.LOCAL_MICROSOFT_ONNX ||
        mode == ServerSource.LOCAL_STABLE_DIFFUSION_CPP ||
        mode == ServerSource.LOCAL_APPLE_BONSAI
    ) {
        1
    } else {
        batchCount
    },
    style = null,
    quality = openAiQuality.key.takeIf {
        mode == ServerSource.OPEN_AI && openAiQuality != OpenAiQuality.AUTO
    },
    openAiModel = openAiModel.takeIf { mode == ServerSource.OPEN_AI },
    stabilityAiClipGuidance = selectedClipGuidancePreset.takeIf { mode == ServerSource.STABILITY_AI },
    stabilityAiStylePreset = selectedStylePreset.takeIf { mode == ServerSource.STABILITY_AI },
    hires = hires.takeIf {
        mode == ServerSource.AUTOMATIC1111 && hires.enabled
    } ?: HiresConfig.DISABLED,
    aDetailer = aDetailer.takeIf {
        mode == ServerSource.AUTOMATIC1111 && aDetailer.enabled && aDetailerAvailable
    } ?: ADetailerConfig.DISABLED,
    forgeModules = selectedForgeModules.takeIf {
        mode == ServerSource.AUTOMATIC1111
    }.orEmpty(),
    falAiModel = falAiModel.takeIf {
        mode == ServerSource.FAL_AI
    } ?: FalAiModel.defaultTextToImage,
    falAiImageSize = falAiImageSize,
    falAiAcceleration = falAiAcceleration,
    sdxlBackend = sdxlBackend.takeIf {
        mode == ServerSource.LOCAL_STABLE_DIFFUSION_CPP
    } ?: SdxlBackend.AUTO,
    falAiSyncMode = falAiSyncMode,
    arliAiModel = arliAiModel.takeIf { mode == ServerSource.ARLI_AI }.orEmpty(),
)

/**
 * Exposes the `DEFAULT_SIZE` value used by the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
internal const val DEFAULT_SIZE = 512
