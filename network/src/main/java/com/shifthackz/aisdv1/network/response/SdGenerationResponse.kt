package com.shifthackz.aisdv1.network.response

import com.google.gson.annotations.SerializedName

data class SdGenerationResponse(
    @SerializedName("images")
    val images: List<String>?,
    @SerializedName("info")
    val info: String?,
) {
    data class Info(
        @SerializedName("seed")
        val seed: Long?,
        @SerializedName("subseed")
        val subSeed: Long?,
        @SerializedName("subseed_strength")
        val subSeedStrength: Float?,
    )
}
