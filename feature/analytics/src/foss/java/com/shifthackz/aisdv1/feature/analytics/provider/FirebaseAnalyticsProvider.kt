package com.shifthackz.aisdv1.feature.analytics.provider

import com.shifthackz.aisdv1.domain.feature.analytics.AnalyticsEvent
import com.shifthackz.aisdv1.feature.analytics.AnalyticsProvider

class FirebaseAnalyticsProvider : AnalyticsProvider {
    override fun create() = Unit
    override fun logEvent(event: AnalyticsEvent) = Unit
}
