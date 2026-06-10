package com.shifthackz.aisdv1.network.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Carries `KtorSwarmUiSessionResponse` data through the SDAI network layer.
 *
 * @author Dmitriy Moroz
 */
@Serializable
data class KtorSwarmUiSessionResponse(
    /**
     * Exposes the `sessionId` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("session_id")
    val sessionId: String? = null,
)
