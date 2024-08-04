package com.shifthackz.aisdv1.network.request

import com.google.gson.annotations.SerializedName

data class SwarmUiModelsRequest(
    @SerializedName("session_id")
    val sessionId: String,
    @SerializedName("subtype")
    val subType: String,
    @SerializedName("path")
    val path: String,
    @SerializedName("depth")
    val depth: Int,
)
