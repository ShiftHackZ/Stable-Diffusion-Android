package com.shifthackz.aisdv1.feature.ads

import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdViewAdListener
import com.applovin.mediation.MaxError
import com.shifthackz.aisdv1.core.common.log.debugLog

interface LoggableMaxAdViewListener : MaxAdViewAdListener {
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
        log("onAdLoadFailed($p0)")
    }

    override fun onAdDisplayFailed(p0: MaxAd?, p1: MaxError?) {
        log("onAdDisplayFailed($p0)")
    }

    override fun onAdExpanded(p0: MaxAd?) {
        log("onAdExpanded($p0)")
    }

    override fun onAdCollapsed(p0: MaxAd?) {
        log("onAdCollapsed($p0)")
    }

    private fun log(msg: String) {
        if (BuildConfig.DEBUG) debugLog(TAG, msg)
    }

    companion object {
        private const val TAG = "AppLovinAd"

        fun factory(): LoggableMaxAdViewListener {
            return object : LoggableMaxAdViewListener {}
        }
    }
}
