package com.shifthackz.aisdv1.app.di

import com.shifthackz.aisdv1.feature.ads.di.adFeatureModule
import com.shifthackz.aisdv1.feature.analytics.di.analyticsModule

val featureModule = (adFeatureModule + analyticsModule).toTypedArray()
