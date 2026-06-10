package com.shifthackz.aisdv1.network.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Carries `StabilityCreditsResponse` data through the SDAI network layer.
 *
 * @author Dmitriy Moroz
 */
@Serializable
data class StabilityCreditsResponse(
    /**
     * Exposes the `credits` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("credits")
    val credits: Float? = null,
)
