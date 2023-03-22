package com.shifthackz.aisdv1.feature.billing.di

import com.shifthackz.aisdv1.domain.feature.billing.BillingFeature
import com.shifthackz.aisdv1.feature.billing.BillingFeatureImpl
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val billingModule = module {
    factoryOf(::BillingFeatureImpl) bind BillingFeature::class
}
