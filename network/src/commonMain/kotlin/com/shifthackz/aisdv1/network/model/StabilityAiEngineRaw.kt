package com.shifthackz.aisdv1.network.model

import kotlinx.serialization.Serializable

/**
 * Carries `StabilityAiEngineRaw` data through the SDAI network layer.
 *
 * @author Dmitriy Moroz
 */
@Serializable
data class StabilityAiEngineRaw(
    /**
     * Exposes the `description` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    val description: String? = null,
    /**
     * Exposes the `id` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    val id: String? = null,
    /**
     * Exposes the `name` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    val name: String? = null,
    /**
     * Exposes the `type` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    val type: String? = null,
)
