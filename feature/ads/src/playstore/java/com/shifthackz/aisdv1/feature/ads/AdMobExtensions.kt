package com.shifthackz.aisdv1.feature.ads

import android.util.Log
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.LoadAdError

fun AdLoader.Builder.applyLoggableAdListener(adId: String = "Unknown") = apply {
    if (BuildConfig.DEBUG) withAdListener(
        object : AdListener() {
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
    )
}
