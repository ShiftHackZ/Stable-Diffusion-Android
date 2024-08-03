package com.shifthackz.aisdv1.core.validation.di

import com.shifthackz.aisdv1.core.validation.common.CommonStringValidator
import com.shifthackz.aisdv1.core.validation.common.CommonStringValidatorImpl
import com.shifthackz.aisdv1.core.validation.dimension.DimensionValidator
import com.shifthackz.aisdv1.core.validation.dimension.DimensionValidatorImpl
import com.shifthackz.aisdv1.core.validation.url.UrlValidator
import com.shifthackz.aisdv1.core.validation.url.UrlValidatorImpl
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val validatorsModule = module {
    // !!! Do not use [factoryOf] for DimensionValidatorImpl, it has 2 default Ints in constructor
    factory<DimensionValidator> { DimensionValidatorImpl() }
    factory<UrlValidator> { UrlValidatorImpl() }

    factoryOf(::CommonStringValidatorImpl) bind CommonStringValidator::class
}
