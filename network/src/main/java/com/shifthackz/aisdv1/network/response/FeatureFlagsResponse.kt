package com.shifthackz.aisdv1.network.response

import com.google.gson.annotations.SerializedName

data class FeatureFlagsResponse(
    @SerializedName("ad_bottom_enable")
    val adBottomEnable: Boolean?,
)
