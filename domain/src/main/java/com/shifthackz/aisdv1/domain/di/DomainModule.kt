package com.shifthackz.aisdv1.domain.di

import com.shifthackz.aisdv1.domain.interactor.settings.SetupConnectionInterActor
import com.shifthackz.aisdv1.domain.interactor.settings.SetupConnectionInterActorImpl
import com.shifthackz.aisdv1.domain.interactor.wakelock.WakeLockInterActor
import com.shifthackz.aisdv1.domain.interactor.wakelock.WakeLockInterActorImpl
import com.shifthackz.aisdv1.domain.usecase.caching.ClearAppCacheUseCase
import com.shifthackz.aisdv1.domain.usecase.caching.ClearAppCacheUseCaseImpl
import com.shifthackz.aisdv1.domain.usecase.caching.DataPreLoaderUseCase
import com.shifthackz.aisdv1.domain.usecase.caching.DataPreLoaderUseCaseImpl
import com.shifthackz.aisdv1.domain.usecase.caching.GetLastResultFromCacheUseCase
import com.shifthackz.aisdv1.domain.usecase.caching.GetLastResultFromCacheUseCaseImpl
import com.shifthackz.aisdv1.domain.usecase.caching.SaveLastResultToCacheUseCase
import com.shifthackz.aisdv1.domain.usecase.caching.SaveLastResultToCacheUseCaseImpl
import com.shifthackz.aisdv1.domain.usecase.connectivity.ObserveSeverConnectivityUseCase
import com.shifthackz.aisdv1.domain.usecase.connectivity.ObserveSeverConnectivityUseCaseImpl
import com.shifthackz.aisdv1.domain.usecase.connectivity.PingStableDiffusionServiceUseCase
import com.shifthackz.aisdv1.domain.usecase.connectivity.PingStableDiffusionServiceUseCaseImpl
import com.shifthackz.aisdv1.domain.usecase.connectivity.TestConnectivityUseCase
import com.shifthackz.aisdv1.domain.usecase.connectivity.TestConnectivityUseCaseImpl
import com.shifthackz.aisdv1.domain.usecase.connectivity.TestHordeApiKeyUseCase
import com.shifthackz.aisdv1.domain.usecase.connectivity.TestHordeApiKeyUseCaseImpl
import com.shifthackz.aisdv1.domain.usecase.connectivity.TestHuggingFaceApiKeyUseCase
import com.shifthackz.aisdv1.domain.usecase.connectivity.TestHuggingFaceApiKeyUseCaseImpl
import com.shifthackz.aisdv1.domain.usecase.connectivity.TestOpenAiApiKeyUseCase
import com.shifthackz.aisdv1.domain.usecase.connectivity.TestOpenAiApiKeyUseCaseImpl
import com.shifthackz.aisdv1.domain.usecase.connectivity.TestStabilityAiApiKeyUseCase
import com.shifthackz.aisdv1.domain.usecase.connectivity.TestStabilityAiApiKeyUseCaseImpl
import com.shifthackz.aisdv1.domain.usecase.connectivity.TestSwarmUiConnectivityUseCase
import com.shifthackz.aisdv1.domain.usecase.connectivity.TestSwarmUiConnectivityUseCaseImpl
import com.shifthackz.aisdv1.domain.usecase.debug.DebugInsertBadBase64UseCase
import com.shifthackz.aisdv1.domain.usecase.debug.DebugInsertBadBase64UseCaseImpl
import com.shifthackz.aisdv1.domain.usecase.donate.FetchAndGetSupportersUseCase
import com.shifthackz.aisdv1.domain.usecase.donate.FetchAndGetSupportersUseCaseImpl
import com.shifthackz.aisdv1.domain.usecase.downloadable.DeleteModelUseCase
import com.shifthackz.aisdv1.domain.usecase.downloadable.DeleteModelUseCaseImpl
import com.shifthackz.aisdv1.domain.usecase.downloadable.DownloadModelUseCase
import com.shifthackz.aisdv1.domain.usecase.downloadable.DownloadModelUseCaseImpl
import com.shifthackz.aisdv1.domain.usecase.downloadable.GetLocalMediaPipeModelsUseCase
import com.shifthackz.aisdv1.domain.usecase.downloadable.GetLocalMediaPipeModelsUseCaseImpl
import com.shifthackz.aisdv1.domain.usecase.downloadable.GetLocalOnnxModelsUseCase
import com.shifthackz.aisdv1.domain.usecase.downloadable.GetLocalOnnxModelsUseCaseImpl
import com.shifthackz.aisdv1.domain.usecase.downloadable.ObserveLocalOnnxModelsUseCase
import com.shifthackz.aisdv1.domain.usecase.downloadable.ObserveLocalOnnxModelsUseCaseImpl
import com.shifthackz.aisdv1.domain.usecase.gallery.DeleteAllGalleryUseCase
import com.shifthackz.aisdv1.domain.usecase.gallery.DeleteAllGalleryUseCaseImpl
import com.shifthackz.aisdv1.domain.usecase.gallery.DeleteGalleryItemUseCase
import com.shifthackz.aisdv1.domain.usecase.gallery.DeleteGalleryItemUseCaseImpl
import com.shifthackz.aisdv1.domain.usecase.gallery.DeleteGalleryItemsUseCase
import com.shifthackz.aisdv1.domain.usecase.gallery.DeleteGalleryItemsUseCaseImpl
import com.shifthackz.aisdv1.domain.usecase.gallery.GetAllGalleryUseCase
import com.shifthackz.aisdv1.domain.usecase.gallery.GetAllGalleryUseCaseImpl
import com.shifthackz.aisdv1.domain.usecase.gallery.GetGalleryItemsUseCase
import com.shifthackz.aisdv1.domain.usecase.gallery.GetGalleryItemsUseCaseImpl
import com.shifthackz.aisdv1.domain.usecase.gallery.GetMediaStoreInfoUseCase
import com.shifthackz.aisdv1.domain.usecase.gallery.GetMediaStoreInfoUseCaseImpl
import com.shifthackz.aisdv1.domain.usecase.gallery.ToggleImageVisibilityUseCase
import com.shifthackz.aisdv1.domain.usecase.gallery.ToggleImageVisibilityUseCaseImpl
import com.shifthackz.aisdv1.domain.usecase.generation.GetGenerationResultPagedUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.GetGenerationResultPagedUseCaseImpl
import com.shifthackz.aisdv1.domain.usecase.generation.GetGenerationResultUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.GetGenerationResultUseCaseImpl
import com.shifthackz.aisdv1.domain.usecase.generation.GetRandomImageUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.GetRandomImageUseCaseImpl
import com.shifthackz.aisdv1.domain.usecase.generation.ImageToImageUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.ImageToImageUseCaseImpl
import com.shifthackz.aisdv1.domain.usecase.generation.InterruptGenerationUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.InterruptGenerationUseCaseImpl
import com.shifthackz.aisdv1.domain.usecase.generation.ObserveHordeProcessStatusUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.ObserveHordeProcessStatusUseCaseImpl
import com.shifthackz.aisdv1.domain.usecase.generation.ObserveLocalDiffusionProcessStatusUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.ObserveLocalDiffusionProcessStatusUseCaseImpl
import com.shifthackz.aisdv1.domain.usecase.generation.SaveGenerationResultUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.SaveGenerationResultUseCaseImpl
import com.shifthackz.aisdv1.domain.usecase.generation.TextToImageUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.TextToImageUseCaseImpl
import com.shifthackz.aisdv1.domain.usecase.huggingface.FetchAndGetHuggingFaceModelsUseCase
import com.shifthackz.aisdv1.domain.usecase.huggingface.FetchAndGetHuggingFaceModelsUseCaseImpl
import com.shifthackz.aisdv1.domain.usecase.report.SendReportUseCase
import com.shifthackz.aisdv1.domain.usecase.report.SendReportUseCaseImpl
import com.shifthackz.aisdv1.domain.usecase.sdembedding.FetchAndGetEmbeddingsUseCase
import com.shifthackz.aisdv1.domain.usecase.sdembedding.FetchAndGetEmbeddingsUseCaseImpl
import com.shifthackz.aisdv1.domain.usecase.sdhypernet.FetchAndGetHyperNetworksUseCase
import com.shifthackz.aisdv1.domain.usecase.sdhypernet.FetchAndGetHyperNetworksUseCaseImpl
import com.shifthackz.aisdv1.domain.usecase.sdlora.FetchAndGetLorasUseCase
import com.shifthackz.aisdv1.domain.usecase.sdlora.FetchAndGetLorasUseCaseImpl
import com.shifthackz.aisdv1.domain.usecase.sdmodel.GetStableDiffusionModelsUseCase
import com.shifthackz.aisdv1.domain.usecase.sdmodel.GetStableDiffusionModelsUseCaseImpl
import com.shifthackz.aisdv1.domain.usecase.sdmodel.SelectStableDiffusionModelUseCase
import com.shifthackz.aisdv1.domain.usecase.sdmodel.SelectStableDiffusionModelUseCaseImpl
import com.shifthackz.aisdv1.domain.usecase.sdsampler.GetStableDiffusionSamplersUseCase
import com.shifthackz.aisdv1.domain.usecase.sdsampler.GetStableDiffusionSamplersUseCaseImpl
import com.shifthackz.aisdv1.domain.usecase.settings.ConnectToA1111UseCase
import com.shifthackz.aisdv1.domain.usecase.settings.ConnectToA1111UseCaseImpl
import com.shifthackz.aisdv1.domain.usecase.settings.ConnectToHordeUseCase
import com.shifthackz.aisdv1.domain.usecase.settings.ConnectToHordeUseCaseImpl
import com.shifthackz.aisdv1.domain.usecase.settings.ConnectToHuggingFaceUseCase
import com.shifthackz.aisdv1.domain.usecase.settings.ConnectToHuggingFaceUseCaseImpl
import com.shifthackz.aisdv1.domain.usecase.settings.ConnectToLocalDiffusionUseCase
import com.shifthackz.aisdv1.domain.usecase.settings.ConnectToLocalDiffusionUseCaseImpl
import com.shifthackz.aisdv1.domain.usecase.settings.ConnectToMediaPipeUseCase
import com.shifthackz.aisdv1.domain.usecase.settings.ConnectToMediaPipeUseCaseImpl
import com.shifthackz.aisdv1.domain.usecase.settings.ConnectToOpenAiUseCase
import com.shifthackz.aisdv1.domain.usecase.settings.ConnectToOpenAiUseCaseImpl
import com.shifthackz.aisdv1.domain.usecase.settings.ConnectToStabilityAiUseCase
import com.shifthackz.aisdv1.domain.usecase.settings.ConnectToStabilityAiUseCaseImpl
import com.shifthackz.aisdv1.domain.usecase.settings.ConnectToSwarmUiUseCase
import com.shifthackz.aisdv1.domain.usecase.settings.ConnectToSwarmUiUseCaseImpl
import com.shifthackz.aisdv1.domain.usecase.settings.GetConfigurationUseCase
import com.shifthackz.aisdv1.domain.usecase.settings.GetConfigurationUseCaseImpl
import com.shifthackz.aisdv1.domain.usecase.settings.SetServerConfigurationUseCase
import com.shifthackz.aisdv1.domain.usecase.settings.SetServerConfigurationUseCaseImpl
import com.shifthackz.aisdv1.domain.usecase.splash.SplashNavigationUseCase
import com.shifthackz.aisdv1.domain.usecase.splash.SplashNavigationUseCaseImpl
import com.shifthackz.aisdv1.domain.usecase.stabilityai.FetchAndGetStabilityAiEnginesUseCase
import com.shifthackz.aisdv1.domain.usecase.stabilityai.FetchAndGetStabilityAiEnginesUseCaseImpl
import com.shifthackz.aisdv1.domain.usecase.stabilityai.ObserveStabilityAiCreditsUseCase
import com.shifthackz.aisdv1.domain.usecase.stabilityai.ObserveStabilityAiCreditsUseCaseImpl
import com.shifthackz.aisdv1.domain.usecase.swarmmodel.FetchAndGetSwarmUiModelsUseCase
import com.shifthackz.aisdv1.domain.usecase.swarmmodel.FetchAndGetSwarmUiModelsUseCaseImpl
import com.shifthackz.aisdv1.domain.usecase.wakelock.AcquireWakelockUseCase
import com.shifthackz.aisdv1.domain.usecase.wakelock.AcquireWakelockUseCaseImpl
import com.shifthackz.aisdv1.domain.usecase.wakelock.ReleaseWakeLockUseCase
import com.shifthackz.aisdv1.domain.usecase.wakelock.ReleaseWakeLockUseCaseImpl
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val useCasesModule = module {
    factoryOf(::TextToImageUseCaseImpl) bind TextToImageUseCase::class
    factoryOf(::ImageToImageUseCaseImpl) bind ImageToImageUseCase::class
    factoryOf(::PingStableDiffusionServiceUseCaseImpl) bind PingStableDiffusionServiceUseCase::class
    factoryOf(::ClearAppCacheUseCaseImpl) bind ClearAppCacheUseCase::class
    factoryOf(::DataPreLoaderUseCaseImpl) bind DataPreLoaderUseCase::class
    factoryOf(::FetchAndGetSwarmUiModelsUseCaseImpl) bind FetchAndGetSwarmUiModelsUseCase::class
    factoryOf(::GetStableDiffusionModelsUseCaseImpl) bind GetStableDiffusionModelsUseCase::class
    factoryOf(::SelectStableDiffusionModelUseCaseImpl) bind SelectStableDiffusionModelUseCase::class
    factoryOf(::GetGenerationResultPagedUseCaseImpl) bind GetGenerationResultPagedUseCase::class
    factoryOf(::GetAllGalleryUseCaseImpl) bind GetAllGalleryUseCase::class
    factoryOf(::GetGalleryItemsUseCaseImpl) bind GetGalleryItemsUseCase::class
    factoryOf(::GetGenerationResultUseCaseImpl) bind GetGenerationResultUseCase::class
    factoryOf(::DeleteGalleryItemUseCaseImpl) bind DeleteGalleryItemUseCase::class
    factoryOf(::DeleteGalleryItemsUseCaseImpl) bind DeleteGalleryItemsUseCase::class
    factoryOf(::DeleteAllGalleryUseCaseImpl) bind DeleteAllGalleryUseCase::class
    factoryOf(::GetStableDiffusionSamplersUseCaseImpl) bind GetStableDiffusionSamplersUseCase::class
    factoryOf(::FetchAndGetLorasUseCaseImpl) bind FetchAndGetLorasUseCase::class
    factoryOf(::FetchAndGetHyperNetworksUseCaseImpl) bind FetchAndGetHyperNetworksUseCase::class
    factoryOf(::FetchAndGetEmbeddingsUseCaseImpl) bind FetchAndGetEmbeddingsUseCase::class
    factoryOf(::SplashNavigationUseCaseImpl) bind SplashNavigationUseCase::class
    factoryOf(::GetConfigurationUseCaseImpl) bind GetConfigurationUseCase::class
    factoryOf(::SetServerConfigurationUseCaseImpl) bind SetServerConfigurationUseCase::class
    factoryOf(::TestConnectivityUseCaseImpl) bind TestConnectivityUseCase::class
    factoryOf(::TestHordeApiKeyUseCaseImpl) bind TestHordeApiKeyUseCase::class
    factoryOf(::TestHuggingFaceApiKeyUseCaseImpl) bind TestHuggingFaceApiKeyUseCase::class
    factoryOf(::TestOpenAiApiKeyUseCaseImpl) bind TestOpenAiApiKeyUseCase::class
    factoryOf(::TestStabilityAiApiKeyUseCaseImpl) bind TestStabilityAiApiKeyUseCase::class
    factoryOf(::TestSwarmUiConnectivityUseCaseImpl) bind TestSwarmUiConnectivityUseCase::class
    factoryOf(::SaveGenerationResultUseCaseImpl) bind SaveGenerationResultUseCase::class
    factoryOf(::ObserveSeverConnectivityUseCaseImpl) bind ObserveSeverConnectivityUseCase::class
    factoryOf(::ObserveHordeProcessStatusUseCaseImpl) bind ObserveHordeProcessStatusUseCase::class
    factoryOf(::GetMediaStoreInfoUseCaseImpl) bind GetMediaStoreInfoUseCase::class
    factoryOf(::ToggleImageVisibilityUseCaseImpl) bind ToggleImageVisibilityUseCase::class
    factoryOf(::GetRandomImageUseCaseImpl) bind GetRandomImageUseCase::class
    factoryOf(::SaveLastResultToCacheUseCaseImpl) bind SaveLastResultToCacheUseCase::class
    factoryOf(::GetLastResultFromCacheUseCaseImpl) bind GetLastResultFromCacheUseCase::class
    factoryOf(::ObserveLocalDiffusionProcessStatusUseCaseImpl) bind ObserveLocalDiffusionProcessStatusUseCase::class
    factoryOf(::GetLocalOnnxModelsUseCaseImpl) bind GetLocalOnnxModelsUseCase::class
    factoryOf(::GetLocalMediaPipeModelsUseCaseImpl) bind GetLocalMediaPipeModelsUseCase::class
    factoryOf(::DownloadModelUseCaseImpl) bind DownloadModelUseCase::class
    factoryOf(::ObserveLocalOnnxModelsUseCaseImpl) bind ObserveLocalOnnxModelsUseCase::class
    factoryOf(::DeleteModelUseCaseImpl) bind DeleteModelUseCase::class
    factoryOf(::AcquireWakelockUseCaseImpl) bind AcquireWakelockUseCase::class
    factoryOf(::ReleaseWakeLockUseCaseImpl) bind ReleaseWakeLockUseCase::class
    factoryOf(::InterruptGenerationUseCaseImpl) bind InterruptGenerationUseCase::class
    factoryOf(::ConnectToHordeUseCaseImpl) bind ConnectToHordeUseCase::class
    factoryOf(::ConnectToLocalDiffusionUseCaseImpl) bind ConnectToLocalDiffusionUseCase::class
    factoryOf(::ConnectToMediaPipeUseCaseImpl) bind ConnectToMediaPipeUseCase::class
    factoryOf(::ConnectToA1111UseCaseImpl) bind ConnectToA1111UseCase::class
    factoryOf(::ConnectToSwarmUiUseCaseImpl) bind ConnectToSwarmUiUseCase::class
    factoryOf(::ConnectToHuggingFaceUseCaseImpl) bind ConnectToHuggingFaceUseCase::class
    factoryOf(::ConnectToOpenAiUseCaseImpl) bind ConnectToOpenAiUseCase::class
    factoryOf(::ConnectToStabilityAiUseCaseImpl) bind ConnectToStabilityAiUseCase::class
    factoryOf(::FetchAndGetHuggingFaceModelsUseCaseImpl) bind FetchAndGetHuggingFaceModelsUseCase::class
    factoryOf(::ObserveStabilityAiCreditsUseCaseImpl) bind ObserveStabilityAiCreditsUseCase::class
    factoryOf(::FetchAndGetStabilityAiEnginesUseCaseImpl) bind FetchAndGetStabilityAiEnginesUseCase::class
    factoryOf(::FetchAndGetSupportersUseCaseImpl) bind FetchAndGetSupportersUseCase::class
    factoryOf(::SendReportUseCaseImpl) bind SendReportUseCase::class
}

internal val interActorsModule = module {
    factoryOf(::WakeLockInterActorImpl) bind WakeLockInterActor::class
    factoryOf(::SetupConnectionInterActorImpl) bind SetupConnectionInterActor::class
}

internal val debugModule = module {
    factoryOf(::DebugInsertBadBase64UseCaseImpl) bind DebugInsertBadBase64UseCase::class
}

val domainModule = (useCasesModule + interActorsModule + debugModule).toTypedArray()
