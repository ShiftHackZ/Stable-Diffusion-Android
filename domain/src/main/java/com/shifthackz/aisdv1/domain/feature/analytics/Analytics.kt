package com.shifthackz.aisdv1.domain.feature.analytics

interface Analytics {
    fun initialize()
    fun logEvent(event: AnalyticsEvent)
}
