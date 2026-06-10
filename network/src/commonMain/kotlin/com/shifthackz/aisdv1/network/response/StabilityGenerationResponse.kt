package com.shifthackz.aisdv1.network.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StabilityGenerationResponse(
    @SerialName("artifacts")
    val artifacts: List<Artifact>? = null,
) {

    @Serializable
    data class Artifact(
        @SerialName("base64")
        val base64: String? = null,
        @SerialName("finishReason")
        val finishReason: String? = null,
        @SerialName("seed")
        val seed: Long? = null,
    )
}
