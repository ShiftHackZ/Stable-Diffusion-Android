package com.shifthackz.aisdv1.network.request

import com.google.gson.annotations.SerializedName

data class HordeGenerationAsyncRequest(
    @SerializedName("prompt")
    val prompt: String,
    @SerializedName("params")
    val params: Params,
    @SerializedName("nsfw")
    val nsfw: Boolean,

) {
    data class Params(
        @SerializedName("cfg_scale")
        val cfgScale: Float,
        @SerializedName("width")
        val width: Int,
        @SerializedName("height")
        val height: Int,
        @SerializedName("steps")
        val steps: Int?,
        @SerializedName("seed")
        val seed: String?,
        @SerializedName("denoising_strength")
        val subSeedStrength: Float?,
    )
}
