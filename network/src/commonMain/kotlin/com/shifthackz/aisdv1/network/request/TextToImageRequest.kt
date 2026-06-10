package com.shifthackz.aisdv1.network.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TextToImageRequest(
    @SerialName("prompt")
    val prompt: String,
    @SerialName("negative_prompt")
    val negativePrompt: String,
    @SerialName("steps")
    val steps: Int,
    @SerialName("cfg_scale")
    val cfgScale: Float,
    @SerialName("width")
    val width: Int,
    @SerialName("height")
    val height: Int,
    @SerialName("batch_size")
    val batchSize: Int,
    @SerialName("restore_faces")
    val restoreFaces: Boolean,
    @SerialName("seed")
    val seed: String?,
    @SerialName("subseed")
    val subSeed: String?,
    @SerialName("subseed_strength")
    val subSeedStrength: Float?,
    @SerialName("sampler_index")
    val samplerIndex: String,
)
