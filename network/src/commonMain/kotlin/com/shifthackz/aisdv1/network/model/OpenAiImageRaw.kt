package com.shifthackz.aisdv1.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OpenAiImageRaw(
    @SerialName("b64_json")
    val b64json: String? = null,
    @SerialName("url")
    val url: String? = null,
    @SerialName("revised_prompt")
    val revisedPrompt: String? = null,
)
