package com.shifthackz.aisdv1.network.response

import com.google.gson.annotations.SerializedName
import com.shifthackz.aisdv1.network.model.SwarmUiModelRaw

data class SwarmUiModelsResponse(
    @SerializedName("files")
    val files: List<SwarmUiModelRaw>?,
)
