package com.shifthackz.aisdv1.feature.ads

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import androidx.annotation.LayoutRes
import com.google.android.gms.ads.*
import com.google.android.gms.ads.nativead.NativeAdView
import com.shifthackz.aisdv1.domain.feature.AdFeature

class AdFeatureImpl : AdFeature {

    override fun initialize(activity: Activity) = MobileAds.initialize(activity)

    override fun getHomeScreenBannerAdView(context: Context) = AdFeature.Ad(
        view = inflateNativeAdView(context, R.layout.native_small_ad_view),
        id = BuildConfig.BANNER_AD_UNIT_ID,
    )

    override fun loadAd(ad: AdFeature.Ad) {
        inflateAdLoader(ad)?.loadAd(AdRequest.Builder().build())
    }

    private fun inflateNativeAdView(context: Context, @LayoutRes layoutId: Int): NativeAdView {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        return inflater.inflate(layoutId, null) as NativeAdView
    }

    private fun inflateAdLoader(ad: AdFeature.Ad) = ad.view?.context?.let { ctx ->
        AdLoader.Builder(ctx, ad.id)
            .forNativeAd { nativeAd -> AdMobRenderer().invoke(ad, nativeAd) }
            .applyLoggableAdListener(ad.id)
            .build()
    }
}
