package com.shifthackz.aisdv1.feature.ads

import android.app.Activity
import android.content.Context
import com.shifthackz.aisdv1.domain.feature.ad.AdFeature

class AdFeatureImpl : AdFeature {
    override fun initialize(activity: Activity) = Unit
    override fun getHomeScreenBannerAd(context: Context) = AdFeature.Ad()
    override fun getGalleryDetailBannerAd(context: Context) = AdFeature.Ad()
    override fun loadAd(ad: AdFeature.Ad) = Unit
}
