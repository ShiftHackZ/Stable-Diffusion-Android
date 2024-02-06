package com.shifthackz.aisdv1.data.mappers

import android.graphics.Bitmap
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import com.shifthackz.aisdv1.network.request.HordeGenerationAsyncRequest
import com.shifthackz.aisdv1.network.request.TextToImageRequest
import com.shifthackz.aisdv1.network.response.SdGenerationResponse
import java.util.Date

fun TextToImagePayload.mapToRequest(): TextToImageRequest = with(this) {
    TextToImageRequest(
        prompt = prompt,
        negativePrompt = negativePrompt,
        steps = samplingSteps,
        cfgScale = cfgScale,
        width = width,
        height = height,
        restoreFaces = restoreFaces,
        seed = seed.trim().ifEmpty { null },
        subSeed = subSeed.trim().ifEmpty { null },
        subSeedStrength = subSeedStrength,
        samplerIndex = sampler,
    )
}

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
        )
    )
}

fun Pair<TextToImagePayload, SdGenerationResponse>.mapToAiGenResult(): AiGenerationResult =
    let { (payload, response) ->
        AiGenerationResult(
            id = 0L,
            image = response.images?.firstOrNull() ?: "",
            inputImage = "",
            createdAt = Date(),
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
            seed = if (payload.seed.trim().isNotEmpty()) payload.seed
            else mapSeedFromRemote(response.info),
            subSeed = if (payload.subSeed.trim().isNotEmpty()) payload.subSeed
            else mapSubSeedFromRemote(response.info),
            subSeedStrength = payload.subSeedStrength,
        )
    }

fun Pair<TextToImagePayload, String>.mapHordeToAiGenResult(): AiGenerationResult =
    let { (payload, base64) ->
        AiGenerationResult(
            id = 0L,
            image = base64,
            inputImage = "",
            createdAt = Date(),
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
        )
    }

fun Pair<TextToImagePayload, String>.mapLocalDiffusionToAiGenResult(): AiGenerationResult =
    let { (payload, base64) ->
        AiGenerationResult(
            id = 0L,
            image = base64,
            inputImage = "",
            createdAt = Date(),
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
        )
    }
