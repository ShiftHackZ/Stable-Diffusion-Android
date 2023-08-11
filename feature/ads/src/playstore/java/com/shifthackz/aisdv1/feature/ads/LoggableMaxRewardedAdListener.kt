package com.shifthackz.aisdv1.feature.ads

import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxError
import com.applovin.mediation.MaxReward
import com.applovin.mediation.MaxRewardedAdListener
import com.shifthackz.aisdv1.core.common.log.debugLog

internal interface LoggableMaxRewardedAdListener : MaxRewardedAdListener {
    override fun onAdLoaded(p0: MaxAd?) {
        log("onAdLoaded($p0)")
    }

    override fun onAdDisplayed(p0: MaxAd?) {
        log("onAdDisplayed($p0)")
    }

    override fun onAdHidden(p0: MaxAd?) {
        log("onAdHidden($p0)")
    }

    override fun onAdClicked(p0: MaxAd?) {
        log("onAdClicked($p0)")
    }

    override fun onAdLoadFailed(p0: String?, p1: MaxError?) {
        log("onAdLoadFailed($p0, $p1)")
    }

    override fun onAdDisplayFailed(p0: MaxAd?, p1: MaxError?) {
        log("onAdDisplayFailed($p0, $p1)")
    }

    override fun onUserRewarded(p0: MaxAd?, p1: MaxReward?) {
        log("onUserRewarded($p0, $p1)")
    }

    @Deprecated("Deprecated in Java")
    override fun onRewardedVideoStarted(p0: MaxAd?) {
        log("onRewardedVideoStarted($p0)")
    }

    @Deprecated("Deprecated in Java")
    override fun onRewardedVideoCompleted(p0: MaxAd?) {
        log("onRewardedVideoCompleted($p0)")
    }

    private fun log(msg: String) {
        if (BuildConfig.DEBUG) debugLog(TAG, msg)
    }

    companion object {
        private const val TAG = "AppLovinRewared"
    }
}
