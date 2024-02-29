package com.shifthackz.aisdv1.network.request

import com.google.gson.annotations.SerializedName

data class OpenAiRequest(
    @SerializedName("prompt")
    val prompt: String,
    @SerializedName("model")
    val model: String = "dall-e-2",
    @SerializedName("size")
    val size: String = "1024x1024",
    @SerializedName("response_format")
    val responseFormat: String = "b64_json",
    @SerializedName("quality")
    val quality: String = "standard",
    @SerializedName("style")
    val style: String? = null,
)
