package com.shifthackz.aisdv1.presentation.di

import com.shifthackz.aisdv1.presentation.activity.AiStableDiffusionViewModel
import com.shifthackz.aisdv1.presentation.modal.embedding.EmbeddingViewModel
import com.shifthackz.aisdv1.presentation.modal.extras.ExtrasViewModel
import com.shifthackz.aisdv1.presentation.modal.history.InputHistoryViewModel
import com.shifthackz.aisdv1.presentation.screen.debug.DebugMenuViewModel
import com.shifthackz.aisdv1.presentation.screen.gallery.detail.GalleryDetailViewModel
import com.shifthackz.aisdv1.presentation.screen.gallery.list.GalleryViewModel
import com.shifthackz.aisdv1.presentation.screen.home.HomeNavigationViewModel
import com.shifthackz.aisdv1.presentation.screen.img2img.ImageToImageViewModel
import com.shifthackz.aisdv1.presentation.screen.loader.ConfigurationLoaderViewModel
import com.shifthackz.aisdv1.presentation.screen.settings.SettingsViewModel
import com.shifthackz.aisdv1.presentation.screen.setup.ServerSetupLaunchSource
import com.shifthackz.aisdv1.presentation.screen.setup.ServerSetupViewModel
import com.shifthackz.aisdv1.presentation.screen.splash.SplashViewModel
import com.shifthackz.aisdv1.presentation.screen.txt2img.TextToImageViewModel
import com.shifthackz.aisdv1.presentation.widget.connectivity.ConnectivityViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::AiStableDiffusionViewModel)
    viewModelOf(::SplashViewModel)
    viewModelOf(::HomeNavigationViewModel)
    viewModelOf(::ConfigurationLoaderViewModel)
    viewModelOf(::ImageToImageViewModel)
    viewModelOf(::TextToImageViewModel)
    viewModelOf(::SettingsViewModel)
    viewModelOf(::GalleryViewModel)
    viewModelOf(::ConnectivityViewModel)
    viewModelOf(::InputHistoryViewModel)
    viewModelOf(::DebugMenuViewModel)
    viewModelOf(::ExtrasViewModel)
    viewModelOf(::EmbeddingViewModel)

    viewModel { parameters ->
        val launchSource = ServerSetupLaunchSource.fromKey(parameters.get())
        ServerSetupViewModel(
            launchSource = launchSource,
            getConfigurationUseCase = get(),
            getLocalAiModelsUseCase = get(),
            fetchAndGetHuggingFaceModelsUseCase = get(),
            urlValidator = get(),
            stringValidator = get(),
            setupConnectionInterActor = get(),
            downloadModelUseCase = get(),
            deleteModelUseCase = get(),
            schedulersProvider = get(),
            preferenceManager = get(),
            analytics = get(),
            wakeLockInterActor = get(),
            router = get(),
        )
    }

    viewModel { parameters ->
        GalleryDetailViewModel(
            itemId = parameters.get(),
            getGenerationResultUseCase = get(),
            getLastResultFromCacheUseCase = get(),
            deleteGalleryItemUseCase = get(),
            galleryDetailBitmapExporter = get(),
            base64ToBitmapConverter = get(),
            schedulersProvider = get(),
            generationFormUpdateEvent = get(),
            analytics = get(),
            router = get(),
        )
    }
}
