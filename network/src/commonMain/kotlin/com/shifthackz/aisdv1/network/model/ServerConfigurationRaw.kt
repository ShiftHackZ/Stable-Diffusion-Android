package com.shifthackz.aisdv1.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ServerConfigurationRaw(
    @SerialName("sd_model_checkpoint")
    val sdModelCheckpoint: String? = null,
)
