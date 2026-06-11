package com.shifthackz.aisdv1.domain.entity

/**
 * Carries `ImageToImagePayload` data through the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
data class ImageToImagePayload(
    /**
     * Exposes the `base64Image` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val base64Image: String,
    /**
     * Exposes the `base64MaskImage` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val base64MaskImage: String,
    /**
     * Exposes the `denoisingStrength` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val denoisingStrength: Float,
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
     * Exposes the `inPaintingMaskInvert` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val inPaintingMaskInvert: Int,
    /**
     * Exposes the `inPaintFullResPadding` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val inPaintFullResPadding: Int,
    /**
     * Exposes the `inPaintingFill` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val inPaintingFill: Int,
    /**
     * Exposes the `inPaintFullRes` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val inPaintFullRes: Boolean,
    /**
     * Exposes the `maskBlur` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val maskBlur: Int,
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
     * Exposes the `falAiModel` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val falAiModel: FalAiModel = FalAiModel.defaultImageToImage,
    /**
     * Exposes the `falAiImageSize` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val falAiImageSize: FalAiImageSize = FalAiImageSize.default,
    /**
     * Exposes the `falAiAcceleration` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val falAiAcceleration: FalAiAcceleration = FalAiAcceleration.default,
    /**
     * Exposes the `falAiSyncMode` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val falAiSyncMode: Boolean = false,
)
