package com.shifthackz.aisdv1.work.mappers

import com.shifthackz.aisdv1.domain.entity.ImageToImagePayload
import com.shifthackz.aisdv1.domain.entity.StabilityAiClipGuidance
import com.shifthackz.aisdv1.domain.entity.StabilityAiStylePreset
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString

internal fun ImageToImagePayload.toByteArray(): ByteArray {
    return payloadJson
        .encodeToString(ImageToImagePayloadDto.from(this))
        .encodeToByteArray()
}

internal fun ByteArray.toImageToImagePayload(): ImageToImagePayload? {
    return runCatching {
        payloadJson
            .decodeFromString<ImageToImagePayloadDto>(decodeToString())
            .toPayload()
    }.getOrNull()
}

@Serializable
private data class ImageToImagePayloadDto(
    val base64Image: String,
    val base64MaskImage: String,
    val denoisingStrength: Float,
    val prompt: String,
    val negativePrompt: String,
    val samplingSteps: Int,
    val cfgScale: Float,
    val width: Int,
    val height: Int,
    val restoreFaces: Boolean,
    val seed: String,
    val subSeed: String,
    val subSeedStrength: Float,
    val sampler: String,
    val nsfw: Boolean,
    val batchCount: Int,
    val inPaintingMaskInvert: Int,
    val inPaintFullResPadding: Int,
    val inPaintingFill: Int,
    val inPaintFullRes: Boolean,
    val maskBlur: Int,
    val stabilityAiClipGuidance: String?,
    val stabilityAiStylePreset: String?,
) {
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

    companion object {
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
