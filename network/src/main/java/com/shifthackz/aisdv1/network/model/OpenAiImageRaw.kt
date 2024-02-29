package com.shifthackz.aisdv1.network.model

import com.google.gson.annotations.SerializedName

/**
 * Response object for OpenAI image.
 *
 * Documentation: https://platform.openai.com/docs/api-reference/images/object
 */
data class OpenAiImageRaw(
    @SerializedName("b64_json")
    val b64json: String?,
    @SerializedName("url")
    val url: String?,
    @SerializedName("revised_prompt")
    val revisedPrompt: String?,
)
