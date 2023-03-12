package com.shifthackz.aisdv1.feature.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import com.google.android.gms.ads.*
import com.shifthackz.aisdv1.domain.feature.AdFeature

class AdFeatureImpl : AdFeature {

    override fun initialize(activity: Activity) {
        MobileAds.initialize(activity)
    }

    override fun getBannerAdView(context: Context): View {
        val adId = BuildConfig.BANNER_AD_UNIT_ID
        val adView = AdView(context)
        adView.setAdSize(AdSize.BANNER)
        adView.adUnitId = adId
        if (BuildConfig.DEBUG) adView.adListener = object : AdListener() {
            override fun onAdClicked() {
                Log.d(this::class.simpleName, "[$adId] onAdClicked")
            }

            override fun onAdClosed() {
                Log.d(this::class.simpleName, "[$adId] onAdClosed")
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d(this::class.simpleName, "[$adId] onAdFailedToLoad : $adError")
            }

            override fun onAdImpression() {
                Log.d(this::class.simpleName, "[$adId] onAdImpression")
            }

            override fun onAdLoaded() {
                Log.d(this::class.simpleName, "[$adId] onAdLoaded")
            }

            override fun onAdOpened() {
                Log.d(this::class.simpleName, "[$adId] onAdOpened")
            }
        }
        return adView
    }

    override fun loadAd(view: View) {
        if (view !is AdView) return
        val adRequest = AdRequest.Builder().build()
        view.loadAd(adRequest)
    }
}
