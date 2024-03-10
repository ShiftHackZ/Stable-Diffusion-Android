package com.shifthackz.aisdv1.network.model

import com.google.gson.annotations.SerializedName

data class StabilityAiEngineRaw(
    @SerializedName("description")
    val description: String?,
    @SerializedName("id")
    val id: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("type")
    val type: String?,
)
