package com.shifthackz.aisdv1.domain.entity

data class ImageToImagePayload(
    val base64Image: String,
    val denoisingStrength: Float,
    val prompt: String,
    val negativePrompt: String,
    val samplingSteps: Int,
    val cfgScale: Float,
    val width: Int,
    val height: Int,
    val restoreFaces: Boolean,
    val seed: String,
    val subSeed: String,
    val subSeedStrength: Float,
    val sampler: String,
)
