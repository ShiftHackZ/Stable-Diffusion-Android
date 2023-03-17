package com.shifthackz.aisdv1.feature.analytics.provider

import android.util.Log
import com.shifthackz.aisdv1.domain.BuildConfig
import com.shifthackz.aisdv1.domain.feature.analytics.AnalyticsEvent
import com.shifthackz.aisdv1.feature.analytics.AnalyticsProvider

class LoggableAnalyticsProvider : AnalyticsProvider {

    override fun create() = Unit

    override fun logEvent(event: AnalyticsEvent) {
        if (BuildConfig.DEBUG) Log.d("Analytics", event.toString())
    }
}
