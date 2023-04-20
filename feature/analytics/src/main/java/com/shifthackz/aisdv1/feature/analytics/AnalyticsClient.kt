package com.shifthackz.aisdv1.feature.analytics

import com.shifthackz.aisdv1.domain.feature.analytics.Analytics
import com.shifthackz.aisdv1.domain.feature.analytics.AnalyticsEvent

internal class AnalyticsClient(
    private val providers: List<AnalyticsProvider>,
) : Analytics {

    override fun initialize() {
        providers.forEach(AnalyticsProvider::create)
    }

    override fun logEvent(event: AnalyticsEvent) {
        providers.forEach { provider -> provider.logEvent(event) }
    }
}
