package com.shifthackz.aisdv1.feature.analytics

import com.shifthackz.aisdv1.domain.feature.analytics.AnalyticsEvent

interface AnalyticsProvider {
    fun create()
    fun logEvent(event: AnalyticsEvent)
}
