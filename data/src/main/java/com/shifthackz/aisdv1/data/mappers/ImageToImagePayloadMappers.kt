package com.shifthackz.aisdv1.data.mappers

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.ImageToImagePayload
import com.shifthackz.aisdv1.network.request.HordeGenerationAsyncRequest
import com.shifthackz.aisdv1.network.request.HuggingFaceGenerationRequest
import com.shifthackz.aisdv1.network.request.ImageToImageRequest
import com.shifthackz.aisdv1.network.request.StabilityImageToImageRequest
import com.shifthackz.aisdv1.network.response.SdGenerationResponse
import java.util.Date

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
        subSeed = subSeed.trim().ifEmpty { null },
        subSeedStrength = subSeedStrength,
        samplerIndex = sampler,
    )
}

fun ImageToImagePayload.mapToHordeRequest(): HordeGenerationAsyncRequest = with(this) {
    HordeGenerationAsyncRequest(
        prompt = prompt,
        nsfw = nsfw,
        sourceProcessing = "img2img",
        sourceImage = base64Image,
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

fun ImageToImagePayload.mapToHuggingFaceRequest(): HuggingFaceGenerationRequest = with(this) {
    HuggingFaceGenerationRequest(
        inputs = base64Image,
        parameters = buildMap {
            this["width"] = width
            this["height"] = height
            prompt.trim().takeIf(String::isNotBlank)?.let {
                this["text"] = it
            }
            negativePrompt.trim().takeIf(String::isNotBlank)?.let {
                this["negative_prompt"] = it
            }
            seed.trim().takeIf(String::isNotBlank)?.let {
                this["seed"] = it
            }
            this["num_inference_steps"] = samplingSteps
            this["guidance_scale"] = cfgScale
        }
    )
}

fun ImageToImagePayload.mapToStabilityAiRequest(): StabilityImageToImageRequest = with(this) {
    StabilityImageToImageRequest(
        textPrompts = buildList {
            addAll(prompt.mapToStabilityPrompt(1.0))
            addAll(negativePrompt.mapToStabilityPrompt(-1.0))
        },
        initImage = base64Image,
        initImageMode = "", //ToDO
        imageStrength = denoisingStrength,
        cfgScale = cfgScale,
        clipGuidancePreset = "", //ToDO
        sampler = sampler,
        seed = seed.toLongOrNull()?.coerceIn(0L .. 4294967295L) ?: 0L,
        steps = samplingSteps,
        stylePreset = null, //ToDO
    )
}

fun Pair<ImageToImagePayload, SdGenerationResponse>.mapToAiGenResult(): AiGenerationResult =
    let { (payload, response) ->
        AiGenerationResult(
            id = 0L,
            image = response.images?.firstOrNull() ?: "",
            inputImage = payload.base64Image,
            createdAt = Date(),
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
            seed = if (payload.seed.trim().isNotEmpty()) payload.seed
            else mapSeedFromRemote(response.info),
            subSeed = if (payload.subSeed.trim().isNotEmpty()) payload.subSeed
            else mapSubSeedFromRemote(response.info),
            subSeedStrength = payload.subSeedStrength,
        )
    }

fun Pair<ImageToImagePayload, String>.mapCloudToAiGenResult(): AiGenerationResult =
    let { (payload, base64) ->
        AiGenerationResult(
            id = 0L,
            image = base64,
            inputImage = payload.base64Image,
            createdAt = Date(),
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
        )
    }
