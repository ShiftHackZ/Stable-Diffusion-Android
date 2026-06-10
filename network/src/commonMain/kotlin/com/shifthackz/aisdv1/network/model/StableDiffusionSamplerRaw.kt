package com.shifthackz.aisdv1.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Carries `StableDiffusionSamplerRaw` data through the SDAI network layer.
 *
 * @author Dmitriy Moroz
 */
@Serializable
data class StableDiffusionSamplerRaw(
    /**
     * Exposes the `name` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("name")
    val name: String? = null,
    /**
     * Exposes the `aliases` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("aliases")
    val aliases: List<String>? = null,
    /**
     * Exposes the `options` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("options")
    val options: Map<String, String>? = null,
)
