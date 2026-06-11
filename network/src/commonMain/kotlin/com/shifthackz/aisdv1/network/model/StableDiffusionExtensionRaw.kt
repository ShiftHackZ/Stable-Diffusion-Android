package com.shifthackz.aisdv1.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Carries A1111 extension metadata through the SDAI network layer.
 *
 * @author Dmitriy Moroz
 */
@Serializable
data class StableDiffusionExtensionRaw(
    /**
     * Exposes the `name` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("name")
    val name: String = "",
    /**
     * Exposes the `enabled` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("enabled")
    val enabled: Boolean = false,
)
