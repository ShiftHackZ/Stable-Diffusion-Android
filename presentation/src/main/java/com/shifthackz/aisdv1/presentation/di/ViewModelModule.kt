package com.shifthackz.aisdv1.presentation.di

import com.shifthackz.aisdv1.presentation.activity.AiStableDiffusionViewModel
import com.shifthackz.aisdv1.presentation.modal.embedding.EmbeddingViewModel
import com.shifthackz.aisdv1.presentation.modal.extras.ExtrasViewModel
import com.shifthackz.aisdv1.presentation.modal.history.InputHistoryViewModel
import com.shifthackz.aisdv1.presentation.modal.tag.EditTagViewModel
import com.shifthackz.aisdv1.presentation.model.LaunchSource
import com.shifthackz.aisdv1.presentation.screen.debug.DebugMenuViewModel
import com.shifthackz.aisdv1.presentation.screen.donate.DonateViewModel
import com.shifthackz.aisdv1.presentation.screen.drawer.DrawerViewModel
import com.shifthackz.aisdv1.presentation.screen.gallery.detail.GalleryDetailViewModel
import com.shifthackz.aisdv1.presentation.screen.gallery.list.GalleryViewModel
import com.shifthackz.aisdv1.presentation.screen.home.HomeNavigationViewModel
import com.shifthackz.aisdv1.presentation.screen.img2img.ImageToImageViewModel
import com.shifthackz.aisdv1.presentation.screen.inpaint.InPaintViewModel
import com.shifthackz.aisdv1.presentation.screen.loader.ConfigurationLoaderViewModel
import com.shifthackz.aisdv1.presentation.screen.logger.LoggerViewModel
import com.shifthackz.aisdv1.presentation.screen.onboarding.OnBoardingViewModel
import com.shifthackz.aisdv1.presentation.screen.report.ReportViewModel
import com.shifthackz.aisdv1.presentation.screen.settings.SettingsViewModel
import com.shifthackz.aisdv1.presentation.screen.setup.ServerSetupViewModel
import com.shifthackz.aisdv1.presentation.screen.splash.SplashViewModel
import com.shifthackz.aisdv1.presentation.screen.txt2img.TextToImageViewModel
import com.shifthackz.aisdv1.presentation.screen.web.webui.WebUiViewModel
import com.shifthackz.aisdv1.presentation.theme.global.AiSdAppThemeViewModel
import com.shifthackz.aisdv1.presentation.widget.connectivity.ConnectivityViewModel
import com.shifthackz.aisdv1.presentation.widget.engine.EngineSelectionViewModel
import com.shifthackz.aisdv1.presentation.widget.work.BackgroundWorkViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::AiStableDiffusionViewModel)
    viewModelOf(::AiSdAppThemeViewModel)
    viewModelOf(::SplashViewModel)
    viewModelOf(::DrawerViewModel)
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
    viewModelOf(::EditTagViewModel)
    viewModelOf(::InPaintViewModel)
    viewModelOf(::EngineSelectionViewModel)
    viewModelOf(::WebUiViewModel)
    viewModelOf(::DonateViewModel)
    viewModelOf(::BackgroundWorkViewModel)
    viewModelOf(::LoggerViewModel)

    viewModel { parameters ->
        OnBoardingViewModel(
            launchSource = LaunchSource.fromKey(parameters.get()),
            dispatchersProvider = get(),
            mainRouter = get(),
            splashNavigationUseCase = get(),
            preferenceManager = get(),
            schedulersProvider = get(),
            buildInfoProvider = get(),
        )
    }

    viewModel { parameters ->
        val launchSource = LaunchSource.fromKey(parameters.get())
        ServerSetupViewModel(
            launchSource = launchSource,
            dispatchersProvider = get(),
            getConfigurationUseCase = get(),
            getLocalOnnxModelsUseCase = get(),
            getLocalMediaPipeModelsUseCase = get(),
            fetchAndGetHuggingFaceModelsUseCase = get(),
            urlValidator = get(),
            stringValidator = get(),
            filePathValidator = get(),
            setupConnectionInterActor = get(),
            downloadModelUseCase = get(),
            deleteModelUseCase = get(),
            schedulersProvider = get(),
            preferenceManager = get(),
            wakeLockInterActor = get(),
            mainRouter = get(),
            buildInfoProvider = get(),
        )
    }

    viewModel { parameters ->
        GalleryDetailViewModel(
            itemId = parameters.get(),
            dispatchersProvider = get(),
            getGenerationResultUseCase = get(),
            getLastResultFromCacheUseCase = get(),
            deleteGalleryItemUseCase = get(),
            toggleImageVisibilityUseCase = get(),
            galleryDetailBitmapExporter = get(),
            base64ToBitmapConverter = get(),
            schedulersProvider = get(),
            generationFormUpdateEvent = get(),
            mainRouter = get(),
        )
    }

    viewModel { parameters ->
        ReportViewModel(
            itemId = parameters.get(),
            sendReportUseCase = get(),
            getGenerationResultUseCase = get(),
            getLastResultFromCacheUseCase = get(),
            base64ToBitmapConverter = get(),
            mainRouter = get(),
            schedulersProvider = get(),
        )
    }
}
