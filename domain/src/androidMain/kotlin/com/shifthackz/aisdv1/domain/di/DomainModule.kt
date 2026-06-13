package com.shifthackz.aisdv1.domain.di

import com.shifthackz.aisdv1.domain.interactor.settings.SetupConnectionInterActor
import com.shifthackz.aisdv1.domain.interactor.settings.SetupConnectionInterActorImpl
import com.shifthackz.aisdv1.domain.usecase.caching.AndroidAppCacheCleaner
import com.shifthackz.aisdv1.domain.usecase.caching.AppCacheCleaner
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * Exposes the `androidDomainOverridesModule` value used by the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
private val androidDomainOverridesModule = module {
    factoryOf(::AndroidAppCacheCleaner) bind AppCacheCleaner::class
    factoryOf(::SetupConnectionInterActorImpl) bind SetupConnectionInterActor::class
}

/**
 * Exposes the `domainModule` value used by the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
val domainModule = arrayOf(androidDomainOverridesModule)
