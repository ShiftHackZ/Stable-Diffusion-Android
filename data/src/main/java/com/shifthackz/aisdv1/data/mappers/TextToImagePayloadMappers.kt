package com.shifthackz.aisdv1.data.mappers

import com.shifthackz.aisdv1.domain.entity.TextToImagePayloadDomain
import com.shifthackz.aisdv1.network.request.TextToImageRequest

fun TextToImagePayloadDomain.mapToRequest(): TextToImageRequest = with(this) {
    TextToImageRequest(
        prompt = prompt,
        negativePrompt = negativePrompt,
        steps = samplingSteps,
        width = width,
        height = height,
        restoreFaces = restoreFaces,
    )
}
