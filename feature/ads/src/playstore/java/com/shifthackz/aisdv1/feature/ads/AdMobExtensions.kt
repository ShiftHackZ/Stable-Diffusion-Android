package com.shifthackz.aisdv1.feature.ads

import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.LoadAdError
import com.shifthackz.aisdv1.core.common.log.debugLog

fun AdLoader.Builder.applyLoggableAdListener(adId: String = "Unknown") = apply {
    if (BuildConfig.DEBUG) withAdListener(
        object : AdListener() {
            override fun onAdClicked() = debugLog("[$adId] onAdClicked")
            override fun onAdClosed() = debugLog("[$adId] onAdClosed")
            override fun onAdFailedToLoad(adError: LoadAdError) = debugLog("[$adId] onAdFailedToLoad : $adError")
            override fun onAdImpression() = debugLog("[$adId] onAdImpression")
            override fun onAdLoaded() = debugLog("[$adId] onAdLoaded")
            override fun onAdOpened() = debugLog("[$adId] onAdOpened")
        }
    )
}
