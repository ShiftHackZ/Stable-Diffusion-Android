package com.shifthackz.aisdv1.data.mappers

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.ImageToImagePayload
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import com.shifthackz.aisdv1.network.request.HuggingFaceGenerationRequest

fun TextToImagePayload.mapToHuggingFaceRequest(): HuggingFaceGenerationRequest = with(this) {
    HuggingFaceGenerationRequest(
        inputs = prompt,
        parameters = HuggingFaceGenerationRequest.Parameters(
            width = width,
            height = height,
            negativePrompt = negativePrompt.trim().takeIf(String::isNotBlank),
            seed = seed.trim().takeIf(String::isNotBlank),
            numInferenceSteps = samplingSteps,
            guidanceScale = cfgScale,
        ),
    )
}

fun ImageToImagePayload.mapToHuggingFaceRequest(): HuggingFaceGenerationRequest = with(this) {
    HuggingFaceGenerationRequest(
        inputs = base64Image,
        parameters = HuggingFaceGenerationRequest.Parameters(
            width = width,
            height = height,
            text = prompt.trim().takeIf(String::isNotBlank),
            negativePrompt = negativePrompt.trim().takeIf(String::isNotBlank),
            seed = seed.trim().takeIf(String::isNotBlank),
            numInferenceSteps = samplingSteps,
            guidanceScale = cfgScale,
        ),
    )
}

fun Pair<TextToImagePayload, String>.mapHuggingFaceTextToImageResult(
    createdAtMillis: Long,
): AiGenerationResult {
    val (payload, base64) = this
    return AiGenerationResult(
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

fun Pair<ImageToImagePayload, String>.mapHuggingFaceImageToImageResult(
    createdAtMillis: Long,
): AiGenerationResult {
    val (payload, base64) = this
    return AiGenerationResult(
        id = 0L,
        image = base64,
        inputImage = payload.base64Image,
        createdAt = createdAtMillis,
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
