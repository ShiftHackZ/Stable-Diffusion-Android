package com.shifthackz.aisdv1.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Carries `ForgeModuleRaw` data through the SDAI network layer.
 *
 * @author Dmitriy Moroz
 */
@Serializable
data class ForgeModuleRaw(
    /**
     * Exposes the `modelName` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("model_name")
    val modelName: String? = null,
    /**
     * Exposes the `filename` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("filename")
    val filename: String? = null,
)
