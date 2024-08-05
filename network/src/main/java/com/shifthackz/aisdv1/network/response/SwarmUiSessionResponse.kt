package com.shifthackz.aisdv1.network.response

import com.google.gson.annotations.SerializedName

data class SwarmUiSessionResponse(
    @SerializedName("session_id")
    val sessionId: String?,
)
