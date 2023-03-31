package com.shifthackz.aisdv1.feature.analytics.provider

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.shifthackz.aisdv1.domain.feature.analytics.AnalyticsEvent
import com.shifthackz.aisdv1.feature.analytics.AnalyticsProvider

internal class FirebaseAnalyticsProvider : AnalyticsProvider {

    private var firebaseAnalytics: FirebaseAnalytics? = null

    override fun create() {
        firebaseAnalytics = Firebase.analytics
    }

    override fun logEvent(event: AnalyticsEvent) {
        if (firebaseAnalytics == null) create()
        if (firebaseAnalytics == null) return
        if (!event.isValid) return
        firebaseAnalytics?.logEvent(event.name) {
            event.parameters
                .filter { (_, value) -> "$value".isNotEmpty() }
                .forEach { (key, value) -> param(key, "$value") }
        }
    }
}
