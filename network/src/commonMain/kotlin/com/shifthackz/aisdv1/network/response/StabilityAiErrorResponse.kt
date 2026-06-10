package com.shifthackz.aisdv1.network.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Carries `StabilityAiErrorResponse` data through the SDAI network layer.
 *
 * @author Dmitriy Moroz
 */
@Serializable
data class StabilityAiErrorResponse(
    /**
     * Exposes the `id` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("id")
    val id: String? = null,
    /**
     * Exposes the `name` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("name")
    val name: String? = null,
    /**
     * Exposes the `message` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("message")
    val message: String? = null,
)
