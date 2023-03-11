package com.shifthackz.aisdv1.core.validation.di

import com.shifthackz.aisdv1.core.validation.dimension.DimensionValidator
import com.shifthackz.aisdv1.core.validation.dimension.DimensionValidatorImpl
import com.shifthackz.aisdv1.core.validation.url.UrlValidator
import com.shifthackz.aisdv1.core.validation.url.UrlValidatorImpl
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val validatorsModule = module {
    factory<DimensionValidator> { DimensionValidatorImpl() }
    factoryOf(::UrlValidatorImpl) bind UrlValidator::class
}
