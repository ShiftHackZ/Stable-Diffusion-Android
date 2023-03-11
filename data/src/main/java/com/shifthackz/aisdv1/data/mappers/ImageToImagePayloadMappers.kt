package com.shifthackz.aisdv1.data.mappers

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.ImageToImagePayload
import com.shifthackz.aisdv1.network.request.ImageToImageRequest
import com.shifthackz.aisdv1.network.response.SdGenerationResponse
import java.util.*

fun ImageToImagePayload.mapToRequest(): ImageToImageRequest = with(this) {
    ImageToImageRequest(
        initImages = listOf(base64Image),
        denoisingStrength = denoisingStrength,
        prompt = prompt,
        negativePrompt = negativePrompt,
        steps = samplingSteps,
        cfgScale = cfgScale,
        width = width,
        height = height,
        restoreFaces = restoreFaces,
        seed = seed.trim().ifEmpty { null },
        samplerIndex = sampler,
    )
}

fun Pair<ImageToImagePayload, SdGenerationResponse>.mapToAiGenResult(): AiGenerationResult =
    let { (payload, response) ->
        AiGenerationResult(
            id = 0L,
            image = response.images.firstOrNull() ?: "",
            inputImage = payload.base64Image,
            createdAt = Date(),
            type = AiGenerationResult.Type.IMAGE_TO_IMAGE,
            prompt = payload.prompt,
            negativePrompt = payload.negativePrompt,
            width = payload.width,
            height = payload.height,
            samplingSteps = payload.samplingSteps,
            cfgScale = payload.cfgScale,
            restoreFaces = payload.restoreFaces,
            sampler = payload.sampler,
            seed = if (payload.seed.trim().isNotEmpty()) payload.seed else mapSeedFromRemote(
                response.info
            ),
        )
    }
