package com.shifthackz.aisdv1.network.model

import kotlinx.serialization.Serializable

/**
 * Carries `StableDiffusionLoraRaw` data through the SDAI network layer.
 *
 * @author Dmitriy Moroz
 */
@Serializable
data class StableDiffusionLoraRaw(
    /**
     * Exposes the `name` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    val name: String? = null,
    /**
     * Exposes the `alias` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    val alias: String? = null,
    /**
     * Exposes the `path` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    val path: String? = null,
)
