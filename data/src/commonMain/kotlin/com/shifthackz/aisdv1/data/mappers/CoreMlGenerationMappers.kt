package com.shifthackz.aisdv1.data.mappers

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.ImageToImagePayload
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import kotlin.time.Clock

/**
 * Converts SDAI data with `mapCoreMlTextToImageResult`.
 *
 * @return Result produced by `mapCoreMlTextToImageResult`.
 * @author Dmitriy Moroz
 */
fun Pair<TextToImagePayload, String>.mapCoreMlTextToImageResult(): AiGenerationResult =
    let { (payload, base64) ->
        AiGenerationResult(
            id = 0L,
            image = base64,
            inputImage = "",
            createdAt = Clock.System.now().toEpochMilliseconds(),
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
 * Converts SDAI data with `mapCoreMlImageToImageResult`.
 *
 * @return Result produced by `mapCoreMlImageToImageResult`.
 * @author Dmitriy Moroz
 */
fun Pair<ImageToImagePayload, String>.mapCoreMlImageToImageResult(): AiGenerationResult =
    let { (payload, base64) ->
        AiGenerationResult(
            id = 0L,
            image = base64,
            inputImage = payload.base64Image,
            createdAt = Clock.System.now().toEpochMilliseconds(),
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
