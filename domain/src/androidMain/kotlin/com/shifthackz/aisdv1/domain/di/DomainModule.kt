package com.shifthackz.aisdv1.domain.di

import com.shifthackz.aisdv1.domain.interactor.settings.SetupConnectionInterActor
import com.shifthackz.aisdv1.domain.interactor.settings.SetupConnectionInterActorImpl
import com.shifthackz.aisdv1.domain.interactor.wakelock.WakeLockInterActor
import com.shifthackz.aisdv1.domain.interactor.wakelock.WakeLockInterActorImpl
import com.shifthackz.aisdv1.domain.usecase.caching.AndroidAppCacheCleaner
import com.shifthackz.aisdv1.domain.usecase.caching.AppCacheCleaner
import com.shifthackz.aisdv1.domain.usecase.wakelock.AcquireWakelockUseCase
import com.shifthackz.aisdv1.domain.usecase.wakelock.AcquireWakelockUseCaseImpl
import com.shifthackz.aisdv1.domain.usecase.wakelock.ReleaseWakeLockUseCase
import com.shifthackz.aisdv1.domain.usecase.wakelock.ReleaseWakeLockUseCaseImpl
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

private val androidDomainOverridesModule = module {
    factoryOf(::AndroidAppCacheCleaner) bind AppCacheCleaner::class
    factoryOf(::AcquireWakelockUseCaseImpl) bind AcquireWakelockUseCase::class
    factoryOf(::ReleaseWakeLockUseCaseImpl) bind ReleaseWakeLockUseCase::class
    factoryOf(::WakeLockInterActorImpl) bind WakeLockInterActor::class
    factoryOf(::SetupConnectionInterActorImpl) bind SetupConnectionInterActor::class
}

val domainModule = arrayOf(androidDomainOverridesModule)
