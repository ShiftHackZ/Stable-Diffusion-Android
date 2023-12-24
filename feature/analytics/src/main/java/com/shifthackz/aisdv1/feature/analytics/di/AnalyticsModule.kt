package com.shifthackz.aisdv1.feature.analytics.di

import com.shifthackz.aisdv1.domain.feature.analytics.Analytics
import com.shifthackz.aisdv1.feature.analytics.AnalyticsClient
import com.shifthackz.aisdv1.feature.analytics.provider.LoggableAnalyticsProvider
import org.koin.dsl.module

val analyticsModule = module {

    single<Analytics> {
        AnalyticsClient(
            listOf(
                LoggableAnalyticsProvider(),
            )
        )
    }
}
