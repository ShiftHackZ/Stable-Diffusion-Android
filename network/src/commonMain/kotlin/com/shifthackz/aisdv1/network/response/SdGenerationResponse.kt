package com.shifthackz.aisdv1.network.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SdGenerationResponse(
    @SerialName("images")
    val images: List<String>? = null,
    @SerialName("info")
    val info: String? = null,
) {
    @Serializable
    data class Info(
        @SerialName("seed")
        val seed: Long? = null,
        @SerialName("all_seeds")
        val allSeeds: List<Long>? = null,
        @SerialName("subseed")
        val subSeed: Long? = null,
        @SerialName("all_subseeds")
        val allSubSeeds: List<Long>? = null,
        @SerialName("subseed_strength")
        val subSeedStrength: Float? = null,
    )
}
