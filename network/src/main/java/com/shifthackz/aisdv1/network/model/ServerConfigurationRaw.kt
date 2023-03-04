package com.shifthackz.aisdv1.network.model

import com.google.gson.annotations.SerializedName

data class ServerConfigurationRaw(
    @SerializedName("sd_model_checkpoint")
    val sdModelCheckpoint: String,
)
//protogenX34Photorealism_1.safetensors [44f90a0972]