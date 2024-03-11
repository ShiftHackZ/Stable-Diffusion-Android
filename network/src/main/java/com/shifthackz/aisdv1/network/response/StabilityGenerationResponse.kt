package com.shifthackz.aisdv1.network.response

import com.google.gson.annotations.SerializedName

data class StabilityGenerationResponse(
    @SerializedName("artifacts")
    val artifacts: List<Artifact>?,
) {

    data class Artifact(
        @SerializedName("base64")
        val base64: String?,
        @SerializedName("finishReason")
        val finishReason: String?,
        @SerializedName("seed")
        val seed: Long?,
    )
}
