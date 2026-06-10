package com.shifthackz.aisdv1.data.mappers

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.ImageToImagePayload
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import com.shifthackz.aisdv1.network.request.ImageToImageRequest
import com.shifthackz.aisdv1.network.request.TextToImageRequest
import com.shifthackz.aisdv1.network.response.SdGenerationResponse
import kotlinx.serialization.json.Json

/**
 * Converts SDAI data with `mapToStableDiffusionRequest`.
 *
 * @author Dmitriy Moroz
 */
fun TextToImagePayload.mapToStableDiffusionRequest(): TextToImageRequest = with(this) {
    TextToImageRequest(
        prompt = prompt,
        negativePrompt = negativePrompt,
        steps = samplingSteps,
        cfgScale = cfgScale,
        width = width,
        height = height,
        batchSize = batchCount,
        restoreFaces = restoreFaces,
        seed = seed.trim().ifEmpty { null },
        subSeed = subSeed.trim().ifEmpty { null },
        subSeedStrength = subSeedStrength,
        samplerIndex = sampler,
    )
}

/**
 * Converts SDAI data with `mapToStableDiffusionRequest`.
 *
 * @author Dmitriy Moroz
 */
fun ImageToImagePayload.mapToStableDiffusionRequest(): ImageToImageRequest = with(this) {
    ImageToImageRequest(
        initImages = listOf(base64Image),
        includeInitImages = true,
        mask = base64MaskImage.takeIf(String::isNotBlank),
        inPaintingMaskInvert = inPaintingMaskInvert,
        inPaintFullResPadding = inPaintFullResPadding,
        inPaintingFill = inPaintingFill,
        inPaintFullRes = inPaintFullRes,
        maskBlur = maskBlur,
        denoisingStrength = denoisingStrength,
        prompt = prompt,
        negativePrompt = negativePrompt,
        steps = samplingSteps,
        cfgScale = cfgScale,
        width = width,
        height = height,
        batchSize = batchCount,
        restoreFaces = restoreFaces,
        seed = seed.trim().ifEmpty { null },
        subSeed = subSeed.trim().ifEmpty { null },
        subSeedStrength = subSeedStrength,
        samplerIndex = sampler,
    )
}

/**
 * Converts SDAI data with `mapStableDiffusionTextToImageResult`.
 *
 * @param createdAtMillis creation timestamp in milliseconds.
 * @return Result produced by `mapStableDiffusionTextToImageResult`.
 * @author Dmitriy Moroz
 */
fun Pair<TextToImagePayload, SdGenerationResponse>.mapStableDiffusionTextToImageResult(
    createdAtMillis: Long,
): List<AiGenerationResult> {
    val (payload, response) = this
    val info = parseInfo(response.info)
    return response.images.orEmpty().mapIndexed { index, image ->
        AiGenerationResult(
            id = 0L,
            image = image,
            inputImage = "",
            createdAt = createdAtMillis + index,
            type = AiGenerationResult.Type.TEXT_TO_IMAGE,
            denoisingStrength = 0f,
            prompt = payload.prompt,
            negativePrompt = payload.negativePrompt,
            width = payload.width,
            height = payload.height,
            samplingSteps = payload.samplingSteps,
            cfgScale = payload.cfgScale,
            restoreFaces = payload.restoreFaces,
            sampler = payload.sampler,
            seed = payload.seed.takeIf { it.trim().isNotEmpty() }
                ?: mapStableDiffusionSeedFromRemote(info, index),
            subSeed = payload.subSeed.takeIf { it.trim().isNotEmpty() }
                ?: mapStableDiffusionSubSeedFromRemote(info, index),
            subSeedStrength = payload.subSeedStrength,
            hidden = false,
        )
    }
}

/**
 * Converts SDAI data with `mapStableDiffusionImageToImageResult`.
 *
 * @param createdAtMillis creation timestamp in milliseconds.
 * @return Result produced by `mapStableDiffusionImageToImageResult`.
 * @author Dmitriy Moroz
 */
fun Pair<ImageToImagePayload, SdGenerationResponse>.mapStableDiffusionImageToImageResult(
    createdAtMillis: Long,
): List<AiGenerationResult> {
    val (payload, response) = this
    val info = parseInfo(response.info)
    return response.images.orEmpty().mapIndexed { index, image ->
        AiGenerationResult(
            id = 0L,
            image = image,
            inputImage = payload.base64Image,
            createdAt = createdAtMillis + index,
            type = AiGenerationResult.Type.IMAGE_TO_IMAGE,
            denoisingStrength = payload.denoisingStrength,
            prompt = payload.prompt,
            negativePrompt = payload.negativePrompt,
            width = payload.width,
            height = payload.height,
            samplingSteps = payload.samplingSteps,
            cfgScale = payload.cfgScale,
            restoreFaces = payload.restoreFaces,
            sampler = payload.sampler,
            seed = payload.seed.takeIf { it.trim().isNotEmpty() }
                ?: mapStableDiffusionSeedFromRemote(info, index),
            subSeed = payload.subSeed.takeIf { it.trim().isNotEmpty() }
                ?: mapStableDiffusionSubSeedFromRemote(info, index),
            subSeedStrength = payload.subSeedStrength,
            hidden = false,
        )
    }
}

/**
 * Converts SDAI data with `mapStableDiffusionSeedFromRemote`.
 *
 * @param info info value consumed by the API.
 * @param index index value consumed by the API.
 * @return Result produced by `mapStableDiffusionSeedFromRemote`.
 * @author Dmitriy Moroz
 */
private fun mapStableDiffusionSeedFromRemote(info: SdGenerationResponse.Info?, index: Int): String =
    info?.allSeeds?.getOrNull(index)?.toString()
        ?: info?.seed?.toString()
        ?: ""

/**
 * Converts SDAI data with `mapStableDiffusionSubSeedFromRemote`.
 *
 * @param info info value consumed by the API.
 * @param index index value consumed by the API.
 * @return Result produced by `mapStableDiffusionSubSeedFromRemote`.
 * @author Dmitriy Moroz
 */
private fun mapStableDiffusionSubSeedFromRemote(info: SdGenerationResponse.Info?, index: Int): String =
    info?.allSubSeeds?.getOrNull(index)?.toString()
        ?: info?.subSeed?.toString()
        ?: ""

/**
 * Executes the `parseInfo` step in the SDAI data layer.
 *
 * @param infoString info string value consumed by the API.
 * @return Result produced by `parseInfo`.
 * @author Dmitriy Moroz
 */
private fun parseInfo(infoString: String?): SdGenerationResponse.Info? = infoString
    ?.takeIf(String::isNotBlank)
    ?.let { raw ->
        runCatching {
            Json {
                ignoreUnknownKeys = true
                isLenient = true
            }.decodeFromString<SdGenerationResponse.Info>(raw)
        }.getOrNull()
    }
