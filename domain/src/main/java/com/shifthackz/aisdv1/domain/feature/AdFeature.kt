package com.shifthackz.aisdv1.domain.feature

import android.app.Activity
import android.content.Context
import android.view.View

interface AdFeature {
    fun initialize(activity: Activity)
    fun getHomeScreenBannerAdView(context: Context): Ad
    fun loadAd(ad: Ad)

    data class Ad(
        val id: String = "",
        val view: View? = null,
    ) {
        val isEmpty: Boolean
            get() = id.isEmpty() || view == null
    }
}
