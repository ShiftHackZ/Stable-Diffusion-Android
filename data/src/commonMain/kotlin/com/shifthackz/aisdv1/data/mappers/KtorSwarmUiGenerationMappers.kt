package com.shifthackz.aisdv1.data.mappers

import com.shifthackz.aisdv1.core.common.math.roundTo
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.ImageToImagePayload
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import com.shifthackz.aisdv1.network.request.SwarmUiGenerationRequest
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

fun TextToImagePayload.mapToKtorSwarmUiRequest(
    sessionId: String,
    swarmUiModel: String,
): SwarmUiGenerationRequest = SwarmUiGenerationRequest(
    sessionId = sessionId,
    model = swarmUiModel,
    initImage = null,
    initImageCreativity = null,
    images = 1,
    prompt = prompt,
    negativePrompt = negativePrompt,
    width = width,
    height = height,
    seed = seed.trim().ifEmpty { null },
    variationSeed = subSeed.trim().ifEmpty { null },
    variationSeedStrength = subSeedStrength.takeIf { it >= 0.1 }?.toString(),
    cfgScale = cfgScale,
    steps = samplingSteps,
)

fun ImageToImagePayload.mapToKtorSwarmUiRequest(
    sessionId: String,
    swarmUiModel: String,
): SwarmUiGenerationRequest = SwarmUiGenerationRequest(
    sessionId = sessionId,
    model = swarmUiModel,
    initImage = base64Image.toPngDataUri(),
    initImageCreativity = denoisingStrength.roundTo(2).toString(),
    images = 1,
    prompt = prompt,
    negativePrompt = negativePrompt,
    width = width,
    height = height,
    seed = seed.trim().ifEmpty { null },
    variationSeed = subSeed.trim().ifEmpty { null },
    variationSeedStrength = subSeedStrength.takeIf { it >= 0.1 }?.toString(),
    cfgScale = cfgScale,
    steps = samplingSteps,
)

fun Pair<TextToImagePayload, String>.mapKtorTextToImageCloudResult(
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

fun Pair<ImageToImagePayload, String>.mapKtorImageToImageCloudResult(
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

@OptIn(ExperimentalEncodingApi::class)
fun ByteArray.encodeBase64NoWrap(): String = Base64.Default.encode(this)

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
