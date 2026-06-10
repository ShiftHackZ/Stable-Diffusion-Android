package com.shifthackz.aisdv1.network.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HordeGenerationAsyncRequest(
    @SerialName("prompt")
    val prompt: String,
    @SerialName("params")
    val params: Params,
    @SerialName("nsfw")
    val nsfw: Boolean,
    @SerialName("source_processing")
    val sourceProcessing: String?,
    @SerialName("source_image")
    val sourceImage: String?,
) {
    @Serializable
    data class Params(
        @SerialName("cfg_scale")
        val cfgScale: Float,
        @SerialName("width")
        val width: Int,
        @SerialName("height")
        val height: Int,
        @SerialName("steps")
        val steps: Int?,
        @SerialName("seed")
        val seed: String?,
        @SerialName("denoising_strength")
        val subSeedStrength: Float?,
    )
}
