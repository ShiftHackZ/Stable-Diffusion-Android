package com.shifthackz.aisdv1.data.mappers

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.shifthackz.aisdv1.domain.entity.AiGenerationResultDomain
import com.shifthackz.aisdv1.domain.entity.TextToImagePayloadDomain
import com.shifthackz.aisdv1.network.request.TextToImageRequest
import com.shifthackz.aisdv1.network.response.TextToImageResponse
import java.util.*

fun TextToImagePayloadDomain.mapToRequest(): TextToImageRequest = with(this) {
    TextToImageRequest(
        prompt = prompt,
        negativePrompt = negativePrompt,
        steps = samplingSteps,
        cfgScale = cfgScale,
        width = width,
        height = height,
        restoreFaces = restoreFaces,
    )
}

fun Pair<TextToImagePayloadDomain, TextToImageResponse>.mapToAiGenResult(): AiGenerationResultDomain =
    with(this) {
        let { (payload, response) ->
            AiGenerationResultDomain(
                0L,
                response.images.firstOrNull() ?: "",
                Date(),
                AiGenerationResultDomain.Type.TEXT_TO_IMAGE,
                payload.prompt,
                payload.negativePrompt,
                payload.width,
                payload.height,
                payload.samplingSteps,
                payload.cfgScale,
                payload.restoreFaces,
                mapSeed(response.info)
            )
        }
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
