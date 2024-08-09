package com.shifthackz.aisdv1.domain.entity

import java.io.Serializable

data class TextToImagePayload(
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
    val style: String?,
    val quality: String?,
    val openAiModel: OpenAiModel?,
    val stabilityAiClipGuidance: StabilityAiClipGuidance?,
    val stabilityAiStylePreset: StabilityAiStylePreset?,
) : Serializable
