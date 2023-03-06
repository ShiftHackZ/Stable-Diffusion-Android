package com.shifthackz.aisdv1.data.mappers

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import com.shifthackz.aisdv1.network.request.TextToImageRequest
import com.shifthackz.aisdv1.network.response.TextToImageResponse
import java.util.*

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
        samplerIndex = sampler,
    )
}

fun Pair<TextToImagePayload, TextToImageResponse>.mapToAiGenResult(): AiGenerationResult =
    let { (payload, response) ->
        AiGenerationResult(
            id = 0L,
            image = response.images.firstOrNull() ?: "",
            createdAt = Date(),
            type = AiGenerationResult.Type.TEXT_TO_IMAGE,
            prompt = payload.prompt,
            negativePrompt = payload.negativePrompt,
            width = payload.width,
            height = payload.height,
            samplingSteps = payload.samplingSteps,
            cfgScale = payload.cfgScale,
            restoreFaces = payload.restoreFaces,
            sampler = payload.sampler,
            seed = if (payload.seed.trim().isNotEmpty()) payload.seed else mapSeed(response.info),
        )
    }


private fun mapSeed(infoString: String): String {
    return try {
        val info = Gson().fromJson<TextToImageResponse.Info>(
            infoString,
            object : TypeToken<TextToImageResponse.Info>() {}.type
        )
        info.seed.toString()
    } catch (e: Exception) {
        ""
    }
}
