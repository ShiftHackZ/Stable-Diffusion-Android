package com.shifthackz.aisdv1.domain.di

import com.shifthackz.aisdv1.domain.interactor.wakelock.*
import com.shifthackz.aisdv1.domain.usecase.caching.*
import com.shifthackz.aisdv1.domain.usecase.connectivity.*
import com.shifthackz.aisdv1.domain.usecase.debug.*
import com.shifthackz.aisdv1.domain.usecase.downloadable.*
import com.shifthackz.aisdv1.domain.usecase.gallery.*
import com.shifthackz.aisdv1.domain.usecase.generation.*
import com.shifthackz.aisdv1.domain.usecase.sdembedding.FetchAndGetEmbeddingsUseCase
import com.shifthackz.aisdv1.domain.usecase.sdembedding.FetchAndGetEmbeddingsUseCaseImpl
import com.shifthackz.aisdv1.domain.usecase.sdhypernet.FetchAndGetHyperNetworksUseCase
import com.shifthackz.aisdv1.domain.usecase.sdhypernet.FetchAndGetHyperNetworksUseCaseImpl
import com.shifthackz.aisdv1.domain.usecase.sdlora.*
import com.shifthackz.aisdv1.domain.usecase.sdmodel.*
import com.shifthackz.aisdv1.domain.usecase.sdsampler.*
import com.shifthackz.aisdv1.domain.usecase.settings.*
import com.shifthackz.aisdv1.domain.usecase.splash.*
import com.shifthackz.aisdv1.domain.usecase.wakelock.*
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val useCasesModule = module {
    factoryOf(::TextToImageUseCaseImpl) bind TextToImageUseCase::class
    factoryOf(::ImageToImageUseCaseImpl) bind ImageToImageUseCase::class
    factoryOf(::PingStableDiffusionServiceUseCaseImpl) bind PingStableDiffusionServiceUseCase::class
    factoryOf(::ClearAppCacheUseCaseImpl) bind ClearAppCacheUseCase::class
    factoryOf(::DataPreLoaderUseCaseImpl) bind DataPreLoaderUseCase::class
    factoryOf(::GetStableDiffusionModelsUseCaseImpl) bind GetStableDiffusionModelsUseCase::class
    factoryOf(::SelectStableDiffusionModelUseCaseImpl) bind SelectStableDiffusionModelUseCase::class
    factoryOf(::GetGenerationResultPagedUseCaseImpl) bind GetGenerationResultPagedUseCase::class
    factoryOf(::GetAllGalleryUseCaseImpl) bind GetAllGalleryUseCase::class
    factoryOf(::GetGenerationResultUseCaseImpl) bind GetGenerationResultUseCase::class
    factoryOf(::DeleteGalleryItemUseCaseImpl) bind DeleteGalleryItemUseCase::class
    factoryOf(::GetStableDiffusionSamplersUseCaseImpl) bind GetStableDiffusionSamplersUseCase::class
    factoryOf(::FetchAndGetLorasUseCaseImpl) bind FetchAndGetLorasUseCase::class
    factoryOf(::FetchAndGetHyperNetworksUseCaseImpl) bind FetchAndGetHyperNetworksUseCase::class
    factoryOf(::FetchAndGetEmbeddingsUseCaseImpl) bind FetchAndGetEmbeddingsUseCase::class
    factoryOf(::SplashNavigationUseCaseImpl) bind SplashNavigationUseCase::class
    factoryOf(::GetConfigurationUseCaseImpl) bind GetConfigurationUseCase::class
    factoryOf(::SetServerConfigurationUseCaseImpl) bind SetServerConfigurationUseCase::class
    factoryOf(::TestConnectivityUseCaseImpl) bind TestConnectivityUseCase::class
    factoryOf(::TestHordeApiKeyUseCaseImpl) bind TestHordeApiKeyUseCase::class
    factoryOf(::SaveGenerationResultUseCaseImpl) bind SaveGenerationResultUseCase::class
    factoryOf(::ObserveSeverConnectivityUseCaseImpl) bind ObserveSeverConnectivityUseCase::class
    factoryOf(::ObserveHordeProcessStatusUseCaseImpl) bind ObserveHordeProcessStatusUseCase::class
    factoryOf(::GetMediaStoreInfoUseCaseImpl) bind GetMediaStoreInfoUseCase::class
    factoryOf(::GetRandomImageUseCaseImpl) bind GetRandomImageUseCase::class
    factoryOf(::SaveLastResultToCacheUseCaseImpl) bind SaveLastResultToCacheUseCase::class
    factoryOf(::GetLastResultFromCacheUseCaseImpl) bind GetLastResultFromCacheUseCase::class
    factoryOf(::ObserveLocalDiffusionProcessStatusUseCaseImpl) bind ObserveLocalDiffusionProcessStatusUseCase::class
    factoryOf(::GetLocalAiModelsUseCaseImpl) bind GetLocalAiModelsUseCase::class
    factoryOf(::DownloadModelUseCaseImpl) bind DownloadModelUseCase::class
    factoryOf(::DeleteModelUseCaseImpl) bind DeleteModelUseCase::class
    factoryOf(::AcquireWakelockUseCaseImpl) bind AcquireWakelockUseCase::class
    factoryOf(::ReleaseWakeLockUseCaseImpl) bind ReleaseWakeLockUseCase::class
}

internal val interActorsModule = module {
    factoryOf(::WakeLockInterActorImpl) bind WakeLockInterActor::class
}

internal val debugModule = module {
    factoryOf(::DebugInsertBadBase64UseCaseImpl) bind DebugInsertBadBase64UseCase::class
}

val domainModule = (useCasesModule + interActorsModule + debugModule).toTypedArray()
