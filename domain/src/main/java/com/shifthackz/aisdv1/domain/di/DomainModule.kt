package com.shifthackz.aisdv1.domain.di

import com.shifthackz.aisdv1.domain.usecase.caching.ClearAppCacheUseCase
import com.shifthackz.aisdv1.domain.usecase.caching.ClearAppCacheUseCaseImpl
import com.shifthackz.aisdv1.domain.usecase.caching.DataPreLoaderUseCase
import com.shifthackz.aisdv1.domain.usecase.caching.DataPreLoaderUseCaseImpl
import com.shifthackz.aisdv1.domain.usecase.coin.EarnRewardedCoinsUseCase
import com.shifthackz.aisdv1.domain.usecase.coin.EarnRewardedCoinsUseCaseImpl
import com.shifthackz.aisdv1.domain.usecase.coin.ObserveCoinsUseCase
import com.shifthackz.aisdv1.domain.usecase.coin.ObserveCoinsUseCaseImpl
import com.shifthackz.aisdv1.domain.usecase.connectivity.ObserveSeverConnectivityUseCase
import com.shifthackz.aisdv1.domain.usecase.connectivity.ObserveSeverConnectivityUseCaseImpl
import com.shifthackz.aisdv1.domain.usecase.connectivity.PingStableDiffusionServiceUseCase
import com.shifthackz.aisdv1.domain.usecase.connectivity.PingStableDiffusionServiceUseCaseImpl
import com.shifthackz.aisdv1.domain.usecase.connectivity.TestConnectivityUseCase
import com.shifthackz.aisdv1.domain.usecase.connectivity.TestConnectivityUseCaseImpl
import com.shifthackz.aisdv1.domain.usecase.gallery.DeleteGalleryItemUseCase
import com.shifthackz.aisdv1.domain.usecase.gallery.DeleteGalleryItemUseCaseImpl
import com.shifthackz.aisdv1.domain.usecase.gallery.GetAllGalleryUseCase
import com.shifthackz.aisdv1.domain.usecase.gallery.GetAllGalleryUseCaseImpl
import com.shifthackz.aisdv1.domain.usecase.gallery.GetGalleryItemUseCase
import com.shifthackz.aisdv1.domain.usecase.gallery.GetGalleryItemUseCaseImpl
import com.shifthackz.aisdv1.domain.usecase.gallery.GetGalleryPageUseCase
import com.shifthackz.aisdv1.domain.usecase.gallery.GetGalleryPageUseCaseImpl
import com.shifthackz.aisdv1.domain.usecase.generation.ImageToImageUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.ImageToImageUseCaseImpl
import com.shifthackz.aisdv1.domain.usecase.generation.SaveGenerationResultUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.SaveGenerationResultUseCaseImpl
import com.shifthackz.aisdv1.domain.usecase.generation.TextToImageUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.TextToImageUseCaseImpl
import com.shifthackz.aisdv1.domain.usecase.motd.ObserveMotdUseCase
import com.shifthackz.aisdv1.domain.usecase.motd.ObserveMotdUseCaseImpl
import com.shifthackz.aisdv1.domain.usecase.sdmodel.GetStableDiffusionModelsUseCase
import com.shifthackz.aisdv1.domain.usecase.sdmodel.GetStableDiffusionModelsUseCaseImpl
import com.shifthackz.aisdv1.domain.usecase.sdmodel.SelectStableDiffusionModelUseCase
import com.shifthackz.aisdv1.domain.usecase.sdmodel.SelectStableDiffusionModelUseCaseImpl
import com.shifthackz.aisdv1.domain.usecase.sdsampler.GetStableDiffusionSamplersUseCase
import com.shifthackz.aisdv1.domain.usecase.sdsampler.GetStableDiffusionSamplersUseCaseImpl
import com.shifthackz.aisdv1.domain.usecase.settings.GetConfigurationUseCase
import com.shifthackz.aisdv1.domain.usecase.settings.GetConfigurationUseCaseImpl
import com.shifthackz.aisdv1.domain.usecase.settings.SetServerConfigurationUseCase
import com.shifthackz.aisdv1.domain.usecase.settings.SetServerConfigurationUseCaseImpl
import com.shifthackz.aisdv1.domain.usecase.splash.SplashNavigationUseCase
import com.shifthackz.aisdv1.domain.usecase.splash.SplashNavigationUseCaseImpl
import com.shifthackz.aisdv1.domain.usecase.version.CheckAppVersionUpdateUseCase
import com.shifthackz.aisdv1.domain.usecase.version.CheckAppVersionUpdateUseCaseImpl
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val domainModule = module {
    factoryOf(::TextToImageUseCaseImpl) bind TextToImageUseCase::class
    factoryOf(::ImageToImageUseCaseImpl) bind ImageToImageUseCase::class
    factoryOf(::PingStableDiffusionServiceUseCaseImpl) bind PingStableDiffusionServiceUseCase::class
    factoryOf(::ClearAppCacheUseCaseImpl) bind ClearAppCacheUseCase::class
    factoryOf(::DataPreLoaderUseCaseImpl) bind DataPreLoaderUseCase::class
    factoryOf(::GetStableDiffusionModelsUseCaseImpl) bind GetStableDiffusionModelsUseCase::class
    factoryOf(::SelectStableDiffusionModelUseCaseImpl) bind SelectStableDiffusionModelUseCase::class
    factoryOf(::GetGalleryPageUseCaseImpl) bind GetGalleryPageUseCase::class
    factoryOf(::GetAllGalleryUseCaseImpl) bind GetAllGalleryUseCase::class
    factoryOf(::GetGalleryItemUseCaseImpl) bind GetGalleryItemUseCase::class
    factoryOf(::DeleteGalleryItemUseCaseImpl) bind DeleteGalleryItemUseCase::class
    factoryOf(::GetStableDiffusionSamplersUseCaseImpl) bind GetStableDiffusionSamplersUseCase::class
    factoryOf(::SplashNavigationUseCaseImpl) bind SplashNavigationUseCase::class
    factoryOf(::GetConfigurationUseCaseImpl) bind GetConfigurationUseCase::class
    factoryOf(::SetServerConfigurationUseCaseImpl) bind SetServerConfigurationUseCase::class
    factoryOf(::TestConnectivityUseCaseImpl) bind TestConnectivityUseCase::class
    factoryOf(::SaveGenerationResultUseCaseImpl) bind SaveGenerationResultUseCase::class
    factoryOf(::ObserveSeverConnectivityUseCaseImpl) bind ObserveSeverConnectivityUseCase::class
    factoryOf(::CheckAppVersionUpdateUseCaseImpl) bind CheckAppVersionUpdateUseCase::class
    factoryOf(::ObserveCoinsUseCaseImpl) bind ObserveCoinsUseCase::class
    factoryOf(::EarnRewardedCoinsUseCaseImpl) bind EarnRewardedCoinsUseCase::class
    factoryOf(::ObserveMotdUseCaseImpl) bind ObserveMotdUseCase::class
}
