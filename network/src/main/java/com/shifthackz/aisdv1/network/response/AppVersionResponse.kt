package com.shifthackz.aisdv1.network.response

import com.google.gson.annotations.SerializedName

data class AppVersionResponse(
    @SerializedName("googleplay")
    val googlePlay: String,
    @SerializedName("fdroid")
    val fDroid: String
)
