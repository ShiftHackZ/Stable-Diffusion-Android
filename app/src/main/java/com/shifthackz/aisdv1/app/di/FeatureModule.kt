package com.shifthackz.aisdv1.app.di

import com.shifthackz.aisdv1.feature.ads.di.adFeatureModule
import com.shifthackz.aisdv1.feature.analytics.di.analyticsModule
import com.shifthackz.aisdv1.feature.auth.di.authModule

val featureModule = (adFeatureModule + analyticsModule + authModule).toTypedArray()
