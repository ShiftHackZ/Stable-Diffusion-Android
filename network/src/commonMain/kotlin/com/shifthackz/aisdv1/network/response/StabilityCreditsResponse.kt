package com.shifthackz.aisdv1.network.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StabilityCreditsResponse(
    @SerialName("credits")
    val credits: Float? = null,
)
