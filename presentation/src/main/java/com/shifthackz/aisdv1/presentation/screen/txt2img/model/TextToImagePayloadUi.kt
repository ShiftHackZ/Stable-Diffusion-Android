package com.shifthackz.aisdv1.presentation.screen.txt2img.model

import com.shifthackz.aisdv1.domain.entity.TextToImagePayloadDomain

data class TextToImagePayloadUi(
    val prompt: String,
    val negativePrompt: String,
    val samplingSteps: Int,
) {
    fun mapToDomain() = TextToImagePayloadDomain(
        prompt = prompt,
        negativePrompt = negativePrompt,
        samplingSteps = samplingSteps,
        width = 512,
        height = 512,
        restoreFaces = true,
    )
}
