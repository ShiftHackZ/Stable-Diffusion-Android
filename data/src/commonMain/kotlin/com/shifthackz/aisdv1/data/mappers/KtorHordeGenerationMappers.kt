package com.shifthackz.aisdv1.data.mappers

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.ImageToImagePayload
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import com.shifthackz.aisdv1.network.request.HordeGenerationAsyncRequest

/**
 * Converts SDAI data with `mapToHordeRequest`.
 *
 * @author Dmitriy Moroz
 */
fun TextToImagePayload.mapToHordeRequest(): HordeGenerationAsyncRequest = with(this) {
    HordeGenerationAsyncRequest(
        prompt = prompt,
        nsfw = nsfw,
        sourceProcessing = null,
        sourceImage = null,
        params = HordeGenerationAsyncRequest.Params(
            cfgScale = cfgScale,
            width = width,
            height = height,
            steps = samplingSteps,
            seed = seed.trim().ifEmpty { null },
            subSeedStrength = subSeedStrength.takeIf { it >= 0.1 },
        ),
    )
}

/**
 * Converts SDAI data with `mapToHordeRequest`.
 *
 * @author Dmitriy Moroz
 */
fun ImageToImagePayload.mapToHordeRequest(): HordeGenerationAsyncRequest = with(this) {
    HordeGenerationAsyncRequest(
        prompt = prompt,
        nsfw = nsfw,
        sourceProcessing = "img2img",
        sourceImage = base64Image,
        params = HordeGenerationAsyncRequest.Params(
            cfgScale = cfgScale,
            width = width,
            height = height,
            steps = samplingSteps,
            seed = seed.trim().ifEmpty { null },
            subSeedStrength = subSeedStrength.takeIf { it >= 0.1 },
        ),
    )
}

/**
 * Converts SDAI data with `mapHordeTextToImageResult`.
 *
 * @param createdAtMillis creation timestamp in milliseconds.
 * @return Result produced by `mapHordeTextToImageResult`.
 * @author Dmitriy Moroz
 */
fun Pair<TextToImagePayload, String>.mapHordeTextToImageResult(
    createdAtMillis: Long,
): AiGenerationResult {
    val (payload, base64) = this
    return AiGenerationResult(
        id = 0L,
        image = base64,
        inputImage = "",
        createdAt = createdAtMillis,
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
        seed = payload.seed,
        subSeed = payload.subSeed,
        subSeedStrength = payload.subSeedStrength,
        hidden = false,
    )
}

/**
 * Converts SDAI data with `mapHordeImageToImageResult`.
 *
 * @param createdAtMillis creation timestamp in milliseconds.
 * @return Result produced by `mapHordeImageToImageResult`.
 * @author Dmitriy Moroz
 */
fun Pair<ImageToImagePayload, String>.mapHordeImageToImageResult(
    createdAtMillis: Long,
): AiGenerationResult {
    val (payload, base64) = this
    return AiGenerationResult(
        id = 0L,
        image = base64,
        inputImage = payload.base64Image,
        createdAt = createdAtMillis,
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
        seed = payload.seed,
        subSeed = payload.subSeed,
        subSeedStrength = payload.subSeedStrength,
        hidden = false,
    )
}
