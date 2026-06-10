package com.shifthackz.aisdv1.network.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class KtorSwarmUiSessionResponse(
    @SerialName("session_id")
    val sessionId: String? = null,
)
