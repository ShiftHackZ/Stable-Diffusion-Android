package com.shifthackz.aisdv1.feature.ads

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import androidx.annotation.LayoutRes
import com.google.android.gms.ads.*
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.shifthackz.aisdv1.core.common.log.errorLog
import com.shifthackz.aisdv1.domain.feature.ad.AdFeature

internal class AdFeatureImpl : AdFeature {

    private var rewardedAd: RewardedAd? = null

    override fun initialize(activity: Activity) = MobileAds.initialize(activity) {
        if (BuildConfig.DEBUG) MobileAds.setRequestConfiguration(
            RequestConfiguration.Builder()
            .setTestDeviceIds(activity.resources.getStringArray(R.array.test_device_ids).asList())
            .build()
        )
        loadRewardedCoinsAd(activity)
    }

    override fun getHomeScreenBannerAd(context: Context) = AdFeature.Ad(
        view = inflateNativeAdView(context, R.layout.native_small_ad_view),
        id = BuildConfig.BANNER_HOMESCREEN_AD_UNIT_ID,
    )

    override fun getGalleryDetailBannerAd(context: Context) = AdFeature.Ad(
        view = inflateNativeAdView(context, R.layout.native_small_ad_view),
        id = BuildConfig.BANNER_GALLERY_AD_UNIT_ID,
    )

    override fun loadAd(ad: AdFeature.Ad) {
        inflateAdLoader(ad)?.loadAd(AdRequest.Builder().build())
    }

    override fun showRewardedCoinsAd(activity: Activity, rewardCallback: (Int) -> Unit) {
        val rewardReducer: (RewardItem) -> Unit = { item ->
            rewardCallback(item.amount)
            loadRewardedCoinsAd(activity)
        }
        val show: (RewardedAd) -> Unit = { ad ->
            ad.show(activity, rewardReducer)
        }
        rewardedAd
            ?.let { ad -> show(ad) }
            ?: run { loadRewardedCoinsAd(activity) { ad -> show(ad) } }
    }

    private fun inflateNativeAdView(context: Context, @LayoutRes layoutId: Int): NativeAdView {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        return inflater.inflate(layoutId, null) as NativeAdView
    }

    private fun inflateAdLoader(ad: AdFeature.Ad) = ad.view?.context?.let { ctx ->
        AdLoader.Builder(ctx, ad.id)
            .forNativeAd { nativeAd -> AdMobXmlRenderer().invoke(ad, nativeAd) }
            .applyLoggableAdListener(ad.id)
            .build()
    }

    private fun loadRewardedCoinsAd(context: Context, onAdReady: (RewardedAd) -> Unit = {}) {
        RewardedAd.load(
            context,
            BuildConfig.COIN_REWARDED_AD_UNIT_ID,
            AdRequest.Builder().build(),
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(p0: RewardedAd) {
                    super.onAdLoaded(p0)
                    rewardedAd = p0
                    onAdReady(p0)
                }

                override fun onAdFailedToLoad(p0: LoadAdError) {
                    super.onAdFailedToLoad(p0)
                    errorLog(Exception(p0.message), "${p0.code} - ${p0.cause}")
                }
            },
        )
    }
}
