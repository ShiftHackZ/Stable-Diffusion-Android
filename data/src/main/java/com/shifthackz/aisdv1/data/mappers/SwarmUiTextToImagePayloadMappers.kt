package com.shifthackz.aisdv1.data.mappers

import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import com.shifthackz.aisdv1.network.request.SwarmUiGenerationRequest

fun TextToImagePayload.mapToSwarmUiRequest(
    sessionId: String,
    swarmUiModel: String,
): SwarmUiGenerationRequest = with(this) {
    SwarmUiGenerationRequest(
        sessionId = sessionId,
        model = swarmUiModel,
        initImage = null,
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
}
//
//fun Pair<TextToImagePayload, SwarmUiGenerationResponse>.mapToAiGenResult(): AiGenerationResult =
//    let { (payload, response) ->
//        AiGenerationResult(
//            id = 0L,
//            image = response.images?.firstOrNull() ?: "",
//            inputImage = "",
//            createdAt = Date(),
//            type = AiGenerationResult.Type.TEXT_TO_IMAGE,
//            denoisingStrength = 0f,
//            prompt = payload.prompt,
//            negativePrompt = payload.negativePrompt,
//            width = payload.width,
//            height = payload.height,
//            samplingSteps = payload.samplingSteps,
//            cfgScale = payload.cfgScale,
//            restoreFaces = payload.restoreFaces,
//            sampler = payload.sampler,
//            seed = payload.seed,
//            subSeed = payload.subSeed,
//            subSeedStrength = payload.subSeedStrength,
//        )
//    }
