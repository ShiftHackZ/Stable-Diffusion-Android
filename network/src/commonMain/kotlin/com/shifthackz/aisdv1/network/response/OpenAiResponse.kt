package com.shifthackz.aisdv1.network.response

import com.shifthackz.aisdv1.network.model.OpenAiImageRaw
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OpenAiResponse(
    @SerialName("created")
    val created: Long? = null,
    @SerialName("data")
    val data: List<OpenAiImageRaw>? = null,
)
