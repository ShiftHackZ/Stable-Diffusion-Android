package com.shifthackz.aisdv1.network.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HordeUserResponse(
    @SerialName("id")
    val id: Int? = null,
)
