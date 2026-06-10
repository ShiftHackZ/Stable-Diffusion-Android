package com.shifthackz.aisdv1.network.model

import kotlinx.serialization.Serializable

/**
 * Carries `StableDiffusionHyperNetworkRaw` data through the SDAI network layer.
 *
 * @author Dmitriy Moroz
 */
@Serializable
data class StableDiffusionHyperNetworkRaw(
    /**
     * Exposes the `name` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    val name: String? = null,
    /**
     * Exposes the `path` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    val path: String? = null,
)
