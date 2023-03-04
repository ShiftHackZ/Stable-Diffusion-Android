package com.shifthackz.aisdv1.core.validation.di

import com.shifthackz.aisdv1.core.validation.dimension.DimensionValidator
import com.shifthackz.aisdv1.core.validation.dimension.DimensionValidatorImpl
import org.koin.dsl.module

val validatorsModule = module {

    factory<DimensionValidator> { DimensionValidatorImpl() }
}
