package com.shifthackz.aisdv1.domain.feature

import android.app.Activity
import android.content.Context
import android.view.View

interface AdFeature {
    fun initialize(activity: Activity)
    fun getBannerAdView(context: Context): View?
    fun loadAd(view: View)
}
