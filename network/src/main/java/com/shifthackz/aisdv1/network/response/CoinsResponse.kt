package com.shifthackz.aisdv1.network.response

import com.google.gson.annotations.SerializedName

data class CoinsResponse(
    @SerializedName("coins_per_day")
    val coinsPerDay: Int?,
)
