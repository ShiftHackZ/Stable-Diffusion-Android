package com.shifthackz.aisdv1.domain.feature.ad

import android.app.Activity
import android.content.Context
import android.view.View

interface AdFeature {
    fun initialize(activity: Activity)
    fun getHomeScreenBannerAd(context: Context): Ad
    fun getGalleryDetailBannerAd(context: Context): Ad
    fun loadAd(ad: Ad)
    fun showRewardedCoinsAd(activity: Activity, rewardCallback: (Int) -> Unit)

    data class Ad(
        val id: String = "",
        val view: View? = null,
    ) {
        val isEmpty: Boolean
            get() = id.isEmpty() || view == null
    }

    companion object {
        val empty = object : AdFeature {
            override fun initialize(activity: Activity) = Unit
            override fun getHomeScreenBannerAd(context: Context) = Ad()
            override fun getGalleryDetailBannerAd(context: Context) = Ad()
            override fun loadAd(ad: Ad) = Unit
            override fun showRewardedCoinsAd(activity: Activity, rewardCallback: (Int) -> Unit) = Unit
        }
    }
}
