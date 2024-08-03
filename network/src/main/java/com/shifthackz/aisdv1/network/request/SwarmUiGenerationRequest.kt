package com.shifthackz.aisdv1.network.request

import com.google.gson.annotations.SerializedName

data class SwarmUiGenerationRequest(
    @SerializedName("session_id")
    val sessionId: String,
    @SerializedName("model")
    val model: String,
    @SerializedName("images")
    val images: Int,
    @SerializedName("prompt")
    val prompt: String,
    @SerializedName("width")
    val width: Int,
    @SerializedName("height")
    val height: Int,
)
