package com.shifthackz.aisdv1.network.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Carries `HordeGenerationAsyncResponse` data through the SDAI network layer.
 *
 * @author Dmitriy Moroz
 */
@Serializable
data class HordeGenerationAsyncResponse(
    /**
     * Exposes the `message` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("message")
    val message: String? = null,
    /**
     * Exposes the `id` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("id")
    val id: String? = null,
    /**
     * Exposes the `kudos` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("kudos")
    val kudos: Double? = null,
)
