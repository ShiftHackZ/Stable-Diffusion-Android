package com.shifthackz.aisdv1.data.mappers

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.OpenAiModel
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import com.shifthackz.aisdv1.network.request.OpenAiRequest

/**
 * Converts SDAI data with `mapToOpenAiRequest`.
 *
 * @author Dmitriy Moroz
 */
fun TextToImagePayload.mapToOpenAiRequest(): OpenAiRequest = with(this) {
    val model = openAiModel ?: OpenAiModel.default
    OpenAiRequest(
        prompt = prompt,
        model = model.alias,
        size = "${width}x${height}",
        quality = quality,
    )
}

/**
 * Converts SDAI data with `mapOpenAiCloudToAiGenResult`.
 *
 * @param createdAtMillis creation timestamp in milliseconds.
 * @author Dmitriy Moroz
 */
fun Pair<TextToImagePayload, String>.mapOpenAiCloudToAiGenResult(
    createdAtMillis: Long,
): AiGenerationResult = let { (payload, base64) ->
    AiGenerationResult(
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
