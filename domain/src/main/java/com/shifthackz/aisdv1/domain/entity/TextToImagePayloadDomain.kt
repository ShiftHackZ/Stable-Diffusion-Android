package com.shifthackz.aisdv1.domain.entity

data class TextToImagePayloadDomain(
    val prompt: String,
    val negativePrompt: String,
    val samplingSteps: Int,
    val width: Int,
    val height: Int,
    val restoreFaces: Boolean,
)
