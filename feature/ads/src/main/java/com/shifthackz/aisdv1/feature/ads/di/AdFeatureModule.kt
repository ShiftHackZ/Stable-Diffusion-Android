package com.shifthackz.aisdv1.feature.ads.di

import com.shifthackz.aisdv1.domain.feature.ad.AdFeature
import com.shifthackz.aisdv1.feature.ads.AdFeatureImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val adFeatureOldModule = module {
    singleOf(::AdFeatureImpl) bind AdFeature::class
}
