package com.shifthackz.aisdv1.network.response

import com.google.gson.annotations.SerializedName

data class HordeGenerationAsyncResponse(
    @SerializedName("message")
    val message: String?,
    @SerializedName("id")
    val id: String?,
    @SerializedName("kudos")
    val kudos: Int?,
)
