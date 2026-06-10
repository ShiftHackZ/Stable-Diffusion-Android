package com.shifthackz.aisdv1.network.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HordeGenerationAsyncResponse(
    @SerialName("message")
    val message: String? = null,
    @SerialName("id")
    val id: String? = null,
    @SerialName("kudos")
    val kudos: Double? = null,
)
