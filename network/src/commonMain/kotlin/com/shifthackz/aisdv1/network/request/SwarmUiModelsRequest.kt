package com.shifthackz.aisdv1.network.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SwarmUiModelsRequest(
    @SerialName("session_id")
    val sessionId: String,
    @SerialName("subtype")
    val subType: String,
    val path: String,
    val depth: Int,
)
