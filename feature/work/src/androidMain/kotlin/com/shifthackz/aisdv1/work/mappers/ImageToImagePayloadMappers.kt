package com.shifthackz.aisdv1.work.mappers

import com.shifthackz.aisdv1.domain.entity.ImageToImagePayload
import com.shifthackz.aisdv1.domain.entity.StabilityAiClipGuidance
import com.shifthackz.aisdv1.domain.entity.StabilityAiStylePreset
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString

/**
 * Converts SDAI data with `toByteArray`.
 *
 * @return Result produced by `toByteArray`.
 * @author Dmitriy Moroz
 */
internal fun ImageToImagePayload.toByteArray(): ByteArray {
    return payloadJson
        .encodeToString(ImageToImagePayloadDto.from(this))
        .encodeToByteArray()
}

/**
 * Converts SDAI data with `toImageToImagePayload`.
 *
 * @return Result produced by `toImageToImagePayload`.
 * @author Dmitriy Moroz
 */
internal fun ByteArray.toImageToImagePayload(): ImageToImagePayload? {
    return runCatching {
        payloadJson
            .decodeFromString<ImageToImagePayloadDto>(decodeToString())
            .toPayload()
    }.getOrNull()
}

/**
 * Carries `ImageToImagePayloadDto` data through the SDAI background work feature layer.
 *
 * @author Dmitriy Moroz
 */
@Serializable
private data class ImageToImagePayloadDto(
    /**
     * Exposes the `base64Image` value used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    val base64Image: String,
    /**
     * Exposes the `base64MaskImage` value used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    val base64MaskImage: String,
    /**
     * Exposes the `denoisingStrength` value used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    val denoisingStrength: Float,
    /**
     * Exposes the `prompt` value used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    val prompt: String,
    /**
     * Exposes the `negativePrompt` value used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    val negativePrompt: String,
    /**
     * Exposes the `samplingSteps` value used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    val samplingSteps: Int,
    /**
     * Exposes the `cfgScale` value used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    val cfgScale: Float,
    /**
     * Exposes the `width` value used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    val width: Int,
    /**
     * Exposes the `height` value used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    val height: Int,
    /**
     * Exposes the `restoreFaces` value used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    val restoreFaces: Boolean,
    /**
     * Exposes the `seed` value used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    val seed: String,
    /**
     * Exposes the `subSeed` value used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    val subSeed: String,
    /**
     * Exposes the `subSeedStrength` value used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    val subSeedStrength: Float,
    /**
     * Exposes the `sampler` value used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    val sampler: String,
    /**
     * Exposes the `nsfw` value used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    val nsfw: Boolean,
    /**
     * Exposes the `batchCount` value used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    val batchCount: Int,
    /**
     * Exposes the `inPaintingMaskInvert` value used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    val inPaintingMaskInvert: Int,
    /**
     * Exposes the `inPaintFullResPadding` value used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    val inPaintFullResPadding: Int,
    /**
     * Exposes the `inPaintingFill` value used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    val inPaintingFill: Int,
    /**
     * Exposes the `inPaintFullRes` value used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    val inPaintFullRes: Boolean,
    /**
     * Exposes the `maskBlur` value used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    val maskBlur: Int,
    /**
     * Exposes the `stabilityAiClipGuidance` value used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    val stabilityAiClipGuidance: String?,
    /**
     * Exposes the `stabilityAiStylePreset` value used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    val stabilityAiStylePreset: String?,
) {
    /**
     * Converts SDAI data with `toPayload`.
     *
     * @author Dmitriy Moroz
     */
    fun toPayload(): ImageToImagePayload = ImageToImagePayload(
        base64Image = base64Image,
        base64MaskImage = base64MaskImage,
        denoisingStrength = denoisingStrength,
        prompt = prompt,
        negativePrompt = negativePrompt,
        samplingSteps = samplingSteps,
        cfgScale = cfgScale,
        width = width,
        height = height,
        restoreFaces = restoreFaces,
        seed = seed,
        subSeed = subSeed,
        subSeedStrength = subSeedStrength,
        sampler = sampler,
        nsfw = nsfw,
        batchCount = batchCount,
        inPaintingMaskInvert = inPaintingMaskInvert,
        inPaintFullResPadding = inPaintFullResPadding,
        inPaintingFill = inPaintingFill,
        inPaintFullRes = inPaintFullRes,
        maskBlur = maskBlur,
        stabilityAiClipGuidance = stabilityAiClipGuidance.parseEnumOrNull<StabilityAiClipGuidance>(),
        stabilityAiStylePreset = stabilityAiStylePreset.parseEnumOrNull<StabilityAiStylePreset>(),
    )

    /**
     * Provides the `companion object` singleton used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    companion object {
        /**
         * Executes the `from` step in the SDAI background work feature layer.
         *
         * @param payload generation payload used by the operation.
         * @author Dmitriy Moroz
         */
        fun from(payload: ImageToImagePayload): ImageToImagePayloadDto = ImageToImagePayloadDto(
            base64Image = payload.base64Image,
            base64MaskImage = payload.base64MaskImage,
            denoisingStrength = payload.denoisingStrength,
            prompt = payload.prompt,
            negativePrompt = payload.negativePrompt,
            samplingSteps = payload.samplingSteps,
            cfgScale = payload.cfgScale,
            width = payload.width,
            height = payload.height,
            restoreFaces = payload.restoreFaces,
            seed = payload.seed,
            subSeed = payload.subSeed,
            subSeedStrength = payload.subSeedStrength,
            sampler = payload.sampler,
            nsfw = payload.nsfw,
            batchCount = payload.batchCount,
            inPaintingMaskInvert = payload.inPaintingMaskInvert,
            inPaintFullResPadding = payload.inPaintFullResPadding,
            inPaintingFill = payload.inPaintingFill,
            inPaintFullRes = payload.inPaintFullRes,
            maskBlur = payload.maskBlur,
            stabilityAiClipGuidance = payload.stabilityAiClipGuidance?.name,
            stabilityAiStylePreset = payload.stabilityAiStylePreset?.name,
        )
    }
}
