package com.shifthackz.aisdv1.data.mappers

import com.shifthackz.aisdv1.domain.entity.ImageToImagePayload
import com.shifthackz.aisdv1.network.request.SwarmUiGenerationRequest

fun ImageToImagePayload.mapToSwarmUiRequest(
    sessionId: String,
    swarmUiModel: String,
): SwarmUiGenerationRequest = with(this) {
    SwarmUiGenerationRequest(
        sessionId = sessionId,
        model = swarmUiModel,
        initImage = "data:image/png;base64,${base64Image.trim('\n').trim('\u003d')}",
//        initImage = base64Image,
//        initImage = base64Image,
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
