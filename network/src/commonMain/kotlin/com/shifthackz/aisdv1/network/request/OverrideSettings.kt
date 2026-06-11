package com.shifthackz.aisdv1.network.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Carries `OverrideSettings` data through the SDAI network layer.
 *
 * @author Dmitriy Moroz
 */
@Serializable
data class OverrideSettings(
    /**
     * Exposes the `forgeAdditionalModules` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("forge_additional_modules")
    val forgeAdditionalModules: List<String>? = null,
)
