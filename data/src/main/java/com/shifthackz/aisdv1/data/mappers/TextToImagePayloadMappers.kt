package com.shifthackz.aisdv1.data.mappers

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.OpenAiModel
import com.shifthackz.aisdv1.domain.entity.StabilityAiClipGuidance
import com.shifthackz.aisdv1.domain.entity.StabilityAiSampler
import com.shifthackz.aisdv1.domain.entity.StabilityAiStylePreset
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import com.shifthackz.aisdv1.network.request.HordeGenerationAsyncRequest
import com.shifthackz.aisdv1.network.request.HuggingFaceGenerationRequest
import com.shifthackz.aisdv1.network.request.OpenAiRequest
import com.shifthackz.aisdv1.network.request.StabilityTextToImageRequest
import com.shifthackz.aisdv1.network.request.SwarmUiGenerationRequest
import com.shifthackz.aisdv1.network.request.TextToImageRequest
import com.shifthackz.aisdv1.network.response.SdGenerationResponse
import java.util.Date

//region PAYLOAD --> REQUEST
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

fun TextToImagePayload.mapToHuggingFaceRequest(): HuggingFaceGenerationRequest = with(this) {
    HuggingFaceGenerationRequest(
        inputs = prompt,
        parameters = buildMap {
            this["width"] = width
            this["height"] = height
            negativePrompt.trim().takeIf(String::isNotBlank)?.let {
                this["negative_prompt"] = it
            }
            seed.trim().takeIf(String::isNotBlank)?.let {
                this["seed"] = it
            }
            this["num_inference_steps"] = samplingSteps
            this["guidance_scale"] = cfgScale
        },
    )
}

fun TextToImagePayload.mapToOpenAiRequest(): OpenAiRequest = with(this) {
    OpenAiRequest(
        prompt = prompt,
        model = openAiModel?.alias ?: OpenAiModel.DALL_E_2.alias,
        size = "${width}x${height}",
        responseFormat = "b64_json",
        quality = quality,
        style = style,
    )
}

fun TextToImagePayload.mapToStabilityAiRequest(): StabilityTextToImageRequest = with(this) {
    StabilityTextToImageRequest(
        height = height,
        width = width,
        textPrompts = buildList {
            addAll(prompt.mapToStabilityPrompt(1.0))
            addAll(negativePrompt.mapToStabilityPrompt(-1.0))
        },
        cfgScale = cfgScale,
        clipGuidancePreset = (stabilityAiClipGuidance ?: StabilityAiClipGuidance.NONE).toString(),
        sampler = sampler
            .takeIf { it != "${StabilityAiSampler.NONE}" }
            .takeIf { StabilityAiSampler.entries.map { s -> "$s" }.contains(it) },
        seed = seed.toLongOrNull()?.coerceIn(0L .. 4294967295L) ?: 0L,
        steps = samplingSteps,
        stylePreset = stabilityAiStylePreset?.takeIf { it != StabilityAiStylePreset.NONE }?.key,
    )
}

fun TextToImagePayload.mapToSwarmUiRequest(
    sessionId: String,
    swarmUiModel: String,
): SwarmUiGenerationRequest = with(this) {
    SwarmUiGenerationRequest(
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
}
//endregion

//region RESPONSE --> RESULT
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

fun Pair<TextToImagePayload, String>.mapCloudToAiGenResult(): AiGenerationResult =
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
//endregion
