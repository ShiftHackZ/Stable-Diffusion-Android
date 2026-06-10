package com.shifthackz.aisdv1.network.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Carries `SwarmUiModelsRequest` data through the SDAI network layer.
 *
 * @author Dmitriy Moroz
 */
@Serializable
data class SwarmUiModelsRequest(
    /**
     * Exposes the `sessionId` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("session_id")
    val sessionId: String,
    /**
     * Exposes the `subType` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("subtype")
    val subType: String,
    /**
     * Exposes the `path` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    val path: String,
    /**
     * Exposes the `depth` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    val depth: Int,
)
