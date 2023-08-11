package com.shifthackz.aisdv1.feature.ads

import android.app.Activity
import android.content.Context
import android.view.ViewGroup
import android.widget.FrameLayout
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxError
import com.applovin.mediation.MaxReward
import com.applovin.mediation.ads.MaxAdView
import com.applovin.mediation.ads.MaxRewardedAd
import com.applovin.sdk.AppLovinSdk
import com.shifthackz.aisdv1.domain.feature.ad.AdFeature

internal class AdFeatureImpl : AdFeature, LoggableMaxRewardedAdListener {

    private var rewardedAd: MaxRewardedAd? = null
    private var rewardCallback: (Int) -> Unit = {}

    override fun initialize(activity: Activity) {
        AppLovinSdk.getInstance(activity).mediationProvider = "max"
        AppLovinSdk.initializeSdk(activity) {
            loadRewardedCoinsAd(activity)
        }
    }

    override fun getHomeScreenBannerAd(context: Context): AdFeature.Ad {
        return loadBannerAd(MaxAdView(BuildConfig.BANNER_HOME_ID, context))
    }

    override fun getGalleryDetailBannerAd(context: Context): AdFeature.Ad {
        return loadBannerAd(MaxAdView(BuildConfig.BANNER_GALLERY_ID, context))
    }

    override fun showRewardedCoinsAd(activity: Activity, rewardCallback: (Int) -> Unit) {
        this.rewardCallback = rewardCallback
        rewardedAd
            ?.takeIf { it.isReady }
            ?.showAd()
    }

    override fun onAdLoadFailed(p0: String?, p1: MaxError?) {
        super.onAdLoadFailed(p0, p1)
        rewardedAd?.loadAd()
    }

    override fun onAdDisplayFailed(p0: MaxAd?, p1: MaxError?) {
        super.onAdDisplayFailed(p0, p1)
        rewardedAd?.loadAd()
    }

    override fun onUserRewarded(p0: MaxAd?, p1: MaxReward?) {
        super.onUserRewarded(p0, p1)
        val amount = p1?.amount?.takeIf { it > 0 } ?: 1
        rewardCallback(amount)
        rewardCallback = {}
    }

    private fun loadRewardedCoinsAd(activity: Activity) {
        rewardedAd = MaxRewardedAd
            .getInstance(BuildConfig.COIN_REWARDED_ID, activity)
            .also { ad -> ad.setListener(this) }
            .also { ad -> ad.loadAd() }
    }

    private fun loadBannerAd(adView: MaxAdView): AdFeature.Ad {
        adView.loadAd()
        adView.setListener(LoggableMaxAdViewListener.factory())
        adView.layoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            adView.context.resources.getDimensionPixelSize(R.dimen.ad_banner_height),
        )
        return AdFeature.Ad(
            id = adView.adUnitId,
            view = adView,
        )
    }
}
