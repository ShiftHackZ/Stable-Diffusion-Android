package com.shifthackz.aisdv1.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Carries `ServerConfigurationRaw` data through the SDAI network layer.
 *
 * @author Dmitriy Moroz
 */
@Serializable
data class ServerConfigurationRaw(
    /**
     * Exposes the `sdModelCheckpoint` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("sd_model_checkpoint")
    val sdModelCheckpoint: String? = null,
)
