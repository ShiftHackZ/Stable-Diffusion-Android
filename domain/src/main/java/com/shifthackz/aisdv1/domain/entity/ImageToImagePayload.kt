package com.shifthackz.aisdv1.domain.entity

import java.io.Serializable

data class ImageToImagePayload(
    val base64Image: String,
    val base64MaskImage: String,
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
    val nsfw: Boolean,
    val batchCount: Int,
    val inPaintingMaskInvert: Int,
    val inPaintFullResPadding: Int,
    val inPaintingFill: Int,
    val inPaintFullRes: Boolean,
    val maskBlur: Int,
    val stabilityAiClipGuidance: StabilityAiClipGuidance?,
    val stabilityAiStylePreset: StabilityAiStylePreset?,
) : Serializable
