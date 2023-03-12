package com.shifthackz.aisdv1.feature.ads

import android.app.Activity
import android.content.Context
import android.view.View
import com.shifthackz.aisdv1.domain.feature.AdFeature

class AdFeatureImpl : AdFeature {

    override fun initialize(activity: Activity) = Unit

    override fun getBannerAdView(context: Context): View? = null

    override fun loadAd(view: View) = Unit
}
