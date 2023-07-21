package com.shifthackz.aisdv1.network.response

import com.google.gson.annotations.SerializedName

data class FeatureFlagsResponse(
    @SerializedName("ad_home_bottom_enable")
    val adHomeBottomEnable: Boolean?,
    @SerializedName("ad_gallery_bottom_enable")
    val adGalleryBottomEnable: Boolean?,
)
