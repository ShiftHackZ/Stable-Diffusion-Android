@file:Suppress("DUPLICATE_LABEL_IN_WHEN")

package com.shifthackz.aisdv1.feature.ads

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.shifthackz.aisdv1.domain.feature.AdFeature

class AdMobXmlRenderer {

    fun invoke(ad: AdFeature.Ad, nativeAd: NativeAd) {
        if (ad.view == null) return
        if (ad.view?.context == null) return
        when (ad.id) {
            BuildConfig.BANNER_HOMESCREEN_AD_UNIT_ID,
            BuildConfig.BANNER_GALLERY_AD_UNIT_ID -> renderHomeScreenBannedAd(ad.view, nativeAd)
        }
    }

    private fun renderHomeScreenBannedAd(adView: View?, nativeAd: NativeAd) {
        if (adView == null) return
        if (adView !is NativeAdView) return
        // Initialize AD title
        val headline = adView.findViewById<TextView>(R.id.nativeAdSmallTitle)
        headline.text = nativeAd.headline
        adView.headlineView = headline
        // Initialize AD image
        val icon = adView.findViewById<ImageView>(R.id.nativeAdSmallImage)
        icon.setImageDrawable(nativeAd.icon?.drawable)
        adView.iconView = icon
        // Initialize AD body
        val body = adView.findViewById<TextView>(R.id.nativeAdSmallDesc)
        body.text = nativeAd.body
        adView.bodyView = body
        // Post initialization stuff
        adView.setNativeAd(nativeAd)
        adView.findViewById<View>(R.id.container).visibility = View.VISIBLE
    }
}
