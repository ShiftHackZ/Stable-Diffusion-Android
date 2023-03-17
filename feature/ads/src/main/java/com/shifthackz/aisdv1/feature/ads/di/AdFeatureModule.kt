package com.shifthackz.aisdv1.feature.ads.di

import com.shifthackz.aisdv1.domain.feature.ad.AdFeature
import com.shifthackz.aisdv1.feature.ads.AdFeatureImpl
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val adFeatureModule = module {
    factoryOf(::AdFeatureImpl) bind AdFeature::class
}
