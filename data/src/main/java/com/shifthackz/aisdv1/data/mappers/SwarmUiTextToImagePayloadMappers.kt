package com.shifthackz.aisdv1.data.mappers

import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import com.shifthackz.aisdv1.network.request.SwarmUiGenerationRequest

fun TextToImagePayload.mapToSwarmUiRequest(sessionId: String): SwarmUiGenerationRequest = with(this) {
    SwarmUiGenerationRequest(
        sessionId = sessionId,
        model = "OfficialStableDiffusion/sd_xl_base_1.0",
        images = 1,
        prompt = prompt,
        width = width,
        height = height,
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
