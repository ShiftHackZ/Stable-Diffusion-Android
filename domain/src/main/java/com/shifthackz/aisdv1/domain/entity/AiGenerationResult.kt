package com.shifthackz.aisdv1.domain.entity

import java.util.Date

data class AiGenerationResult(
    val id: Long,
    val image: String,
    val inputImage: String,
    val createdAt: Date,
    val type: Type,
    val prompt: String,
    val negativePrompt: String,
    val width: Int,
    val height: Int,
    val samplingSteps: Int,
    val cfgScale: Float,
    val restoreFaces: Boolean,
    val sampler: String,
    val seed: String,
    val subSeed: String,
    val subSeedStrength: Float,
    val denoisingStrength: Float,
) {
    enum class Type(val key: String) {
        TEXT_TO_IMAGE("txt2img"),
        IMAGE_TO_IMAGE("img2img");

        companion object {
            fun parse(input: String?) = entries
                .find { it.key == input } ?: TEXT_TO_IMAGE
        }
    }
}
