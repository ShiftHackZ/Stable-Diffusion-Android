package com.shifthackz.aisdv1.domain.entity

/**
 * Carries `TextToImagePayload` data through the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
data class TextToImagePayload(
    /**
     * Exposes the `prompt` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val prompt: String,
    /**
     * Exposes the `negativePrompt` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val negativePrompt: String,
    /**
     * Exposes the `samplingSteps` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val samplingSteps: Int,
    /**
     * Exposes the `cfgScale` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val cfgScale: Float,
    /**
     * Exposes the `width` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val width: Int,
    /**
     * Exposes the `height` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val height: Int,
    /**
     * Exposes the `restoreFaces` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val restoreFaces: Boolean,
    /**
     * Exposes the `seed` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val seed: String,
    /**
     * Exposes the `subSeed` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val subSeed: String,
    /**
     * Exposes the `subSeedStrength` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val subSeedStrength: Float,
    /**
     * Exposes the `sampler` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val sampler: String,
    /**
     * Exposes the `scheduler` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val scheduler: Scheduler = Scheduler.AUTOMATIC,
    /**
     * Exposes the `nsfw` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val nsfw: Boolean,
    /**
     * Exposes the `batchCount` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val batchCount: Int,
    /**
     * Exposes the `style` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val style: String?,
    /**
     * Exposes the `quality` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val quality: String?,
    /**
     * Exposes the `openAiModel` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val openAiModel: OpenAiModel?,
    /**
     * Exposes the `stabilityAiClipGuidance` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val stabilityAiClipGuidance: StabilityAiClipGuidance?,
    /**
     * Exposes the `stabilityAiStylePreset` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val stabilityAiStylePreset: StabilityAiStylePreset?,
    /**
     * Exposes the `aDetailer` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val aDetailer: ADetailerConfig = ADetailerConfig.DISABLED,
    /**
     * Exposes the `hires` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val hires: HiresConfig = HiresConfig.DISABLED,
    /**
     * Exposes the `forgeModules` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val forgeModules: List<ForgeModule> = emptyList(),
)
