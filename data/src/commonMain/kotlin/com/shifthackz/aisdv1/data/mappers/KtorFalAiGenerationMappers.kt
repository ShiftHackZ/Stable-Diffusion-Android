package com.shifthackz.aisdv1.data.mappers

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.FalAiModel
import com.shifthackz.aisdv1.domain.entity.ImageToImagePayload
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import com.shifthackz.aisdv1.network.request.FalAiImageToImageRequest
import com.shifthackz.aisdv1.network.request.FalAiTextToImageRequest
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

/**
 * Converts SDAI data with `mapToFalAiRequest`.
 *
 * @return Result produced by `mapToFalAiRequest`.
 * @author Dmitriy Moroz
 */
fun TextToImagePayload.mapToFalAiRequest(): FalAiTextToImageRequest = with(this) {
    FalAiTextToImageRequest(
        prompt = prompt,
        imageSize = falAiImageSize.key,
        numInferenceSteps = samplingSteps.coerceIn(
            falAiModel.minInferenceSteps,
            falAiModel.maxInferenceSteps,
        ),
        guidanceScale = cfgScale.coerceIn(
            falAiModel.minGuidanceScale,
            falAiModel.maxGuidanceScale,
        ),
        seed = seed.trim()
            .toLongOrNull()
            ?.takeIf { value -> value >= 0L },
        syncMode = falAiSyncMode,
        numImages = batchCount.coerceIn(MIN_BATCH_COUNT, MAX_BATCH_COUNT),
        enableSafetyChecker = !nsfw,
        acceleration = falAiAcceleration
            .takeIf(falAiModel.supportedAccelerations::contains)
            ?.key
            ?: falAiModel.supportedAccelerations.first().key,
    )
}

/**
 * Converts SDAI data with `mapToFalAiRequest`.
 *
 * @return Result produced by `mapToFalAiRequest`.
 * @author Dmitriy Moroz
 */
fun ImageToImagePayload.mapToFalAiRequest(): FalAiImageToImageRequest = with(this) {
    FalAiImageToImageRequest(
        imageUrl = base64Image.toPngDataUri(),
        prompt = prompt,
        strength = denoisingStrength.coerceIn(MIN_STRENGTH, MAX_STRENGTH),
        imageSize = falAiImageSize.key.takeIf { falAiModel.supportsImageSize },
        numInferenceSteps = samplingSteps.coerceIn(
            falAiModel.minInferenceSteps,
            falAiModel.maxInferenceSteps,
        ),
        guidanceScale = cfgScale.coerceIn(
            falAiModel.minGuidanceScale,
            falAiModel.maxGuidanceScale,
        ),
        seed = seed.trim()
            .toLongOrNull()
            ?.takeIf { value -> value >= 0L },
        syncMode = falAiSyncMode,
        numImages = batchCount.coerceIn(MIN_BATCH_COUNT, MAX_BATCH_COUNT),
        enableSafetyChecker = !nsfw,
        acceleration = falAiAcceleration
            .takeIf(falAiModel.supportedAccelerations::contains)
            ?.key
            ?: falAiModel.supportedAccelerations.first().key,
    )
}

/**
 * Converts SDAI data with `mapFalAiTextToImageResult`.
 *
 * @param base64 generated image payload.
 * @param responseSeed seed returned by Fal.ai.
 * @param createdAtMillis creation timestamp in milliseconds.
 * @return Result produced by `mapFalAiTextToImageResult`.
 * @author Dmitriy Moroz
 */
fun TextToImagePayload.mapFalAiTextToImageResult(
    base64: String,
    responseSeed: Long?,
    createdAtMillis: Long,
): AiGenerationResult = AiGenerationResult(
    id = 0L,
    image = base64,
    inputImage = "",
    createdAt = createdAtMillis,
    type = AiGenerationResult.Type.TEXT_TO_IMAGE,
    denoisingStrength = 0f,
    prompt = prompt,
    negativePrompt = negativePrompt,
    width = width,
    height = height,
    samplingSteps = samplingSteps,
    cfgScale = cfgScale,
    restoreFaces = restoreFaces,
    sampler = sampler,
    seed = responseSeed?.toString() ?: seed,
    subSeed = subSeed,
    subSeedStrength = subSeedStrength,
    hidden = false,
)

/**
 * Converts SDAI data with `mapFalAiImageToImageResult`.
 *
 * @param base64 generated image payload.
 * @param responseSeed seed returned by Fal.ai.
 * @param createdAtMillis creation timestamp in milliseconds.
 * @return Result produced by `mapFalAiImageToImageResult`.
 * @author Dmitriy Moroz
 */
fun ImageToImagePayload.mapFalAiImageToImageResult(
    base64: String,
    responseSeed: Long?,
    createdAtMillis: Long,
): AiGenerationResult = AiGenerationResult(
    id = 0L,
    image = base64,
    inputImage = base64Image,
    createdAt = createdAtMillis,
    type = AiGenerationResult.Type.IMAGE_TO_IMAGE,
    denoisingStrength = denoisingStrength,
    prompt = prompt,
    negativePrompt = negativePrompt,
    width = width,
    height = height,
    samplingSteps = samplingSteps,
    cfgScale = cfgScale,
    restoreFaces = restoreFaces,
    sampler = sampler,
    seed = responseSeed?.toString() ?: seed,
    subSeed = subSeed,
    subSeedStrength = subSeedStrength,
    hidden = false,
)

/**
 * Converts SDAI data with `toModelName`.
 *
 * @return Result produced by `toModelName`.
 * @author Dmitriy Moroz
 */
fun FalAiModel.toModelName(): String = displayName

/**
 * Converts SDAI data with `toPngDataUri`.
 *
 * @return Result produced by `toPngDataUri`.
 * @author Dmitriy Moroz
 */
@OptIn(ExperimentalEncodingApi::class)
private fun String.toPngDataUri(): String {
    val normalized = substringAfter("base64,", this)
        .filterNot(Char::isWhitespace)
        .let { base64 ->
            runCatching { Base64.Default.encode(Base64.Default.decode(base64)) }
                .getOrElse { base64 }
        }
    return "data:image/png;base64,$normalized"
}

private const val MIN_BATCH_COUNT = 1
private const val MAX_BATCH_COUNT = 4
private const val MIN_STRENGTH = 0.01f
private const val MAX_STRENGTH = 1f
