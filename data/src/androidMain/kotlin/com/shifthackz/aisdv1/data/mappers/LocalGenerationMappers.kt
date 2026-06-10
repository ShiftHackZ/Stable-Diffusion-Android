package com.shifthackz.aisdv1.data.mappers

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload

/**
 * Converts SDAI data with `mapLocalDiffusionToAiGenResult`.
 *
 * @return Result produced by `mapLocalDiffusionToAiGenResult`.
 * @author Dmitriy Moroz
 */
fun Pair<TextToImagePayload, String>.mapLocalDiffusionToAiGenResult(): AiGenerationResult =
    let { (payload, base64) ->
        AiGenerationResult(
            id = 0L,
            image = base64,
            inputImage = "",
            createdAt = System.currentTimeMillis(),
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
