package com.shifthackz.aisdv1.network.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OpenAiRequest(
    @SerialName("prompt")
    val prompt: String,
    @SerialName("model")
    val model: String,
    @SerialName("size")
    val size: String,
    @SerialName("quality")
    val quality: String? = null,
)
