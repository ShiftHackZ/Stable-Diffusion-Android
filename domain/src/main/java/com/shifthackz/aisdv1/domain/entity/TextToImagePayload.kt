package com.shifthackz.aisdv1.domain.entity

data class TextToImagePayload(
    val prompt: String,
    val negativePrompt: String,
    val samplingSteps: Int,
    val cfgScale: Float,
    val width: Int,
    val height: Int,
    val restoreFaces: Boolean,
    val sampler: String,
)
