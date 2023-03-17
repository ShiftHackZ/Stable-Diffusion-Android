package com.shifthackz.aisdv1.domain.feature.ad

import android.app.Activity
import android.content.Context
import android.view.View

interface AdFeature {
    fun initialize(activity: Activity)
    fun getHomeScreenBannerAd(context: Context): Ad
    fun getGalleryDetailBannerAd(context: Context): Ad
    fun loadAd(ad: Ad)

    data class Ad(
        val id: String = "",
        val view: View? = null,
    ) {
        val isEmpty: Boolean
            get() = id.isEmpty() || view == null
    }
}
