package com.shifthackz.aisdv1.feature.ads

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.shifthackz.aisdv1.domain.feature.AdFeature

class AdMobRenderer {

    operator fun invoke(ad: AdFeature.Ad, nativeAd: NativeAd) {
        if (ad.view == null) return
        if (ad.view?.context == null) return
        when (ad.id) {
            BuildConfig.BANNER_AD_UNIT_ID -> renderHomeScreenBannedAd(ad.view, nativeAd)
        }
    }

    private fun renderHomeScreenBannedAd(adView: View?, nativeAd: NativeAd) {
        if (adView == null) return
        if (adView !is NativeAdView) return
        val headline = adView.findViewById<TextView>(R.id.nativeAdSmallTitle)
        headline.text = nativeAd.headline
        adView.headlineView = headline

        val advertiser = adView.findViewById<TextView>(R.id.nativeAdSmallName)
        advertiser.text = nativeAd.advertiser
        adView.advertiserView = adView

        val icon = adView.findViewById<ImageView>(R.id.nativeAdSmallImage)
        icon.setImageDrawable(nativeAd.icon?.drawable)
        adView.iconView = icon

//            val mediaView = adView.findViewById<MediaView>(R.id.nativeAdSmallMediaView)
//            adView.mediaView = mediaView

        val button = adView.findViewById<Button>(R.id.nativeAdSmallButton)
        adView.callToActionView = button
        button.setText(nativeAd.store)

        val body = adView.findViewById<TextView>(R.id.nativeAdSmallDesc)
        body.text = nativeAd.body
        adView.bodyView = body

//            val price = adView.findViewById<TextView>(R.id.nativeAdSmallPrice)
//            price.text = ad.price
//            adView.priceView = price

        adView.setNativeAd(nativeAd)


        //Show
    }
}
