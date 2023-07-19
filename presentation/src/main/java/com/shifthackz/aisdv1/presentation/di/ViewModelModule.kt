package com.shifthackz.aisdv1.presentation.di

import com.shifthackz.aisdv1.core.common.links.LinksProvider
import com.shifthackz.aisdv1.presentation.activity.AiStableDiffusionViewModel
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
import com.shifthackz.aisdv1.presentation.widget.coins.AvailableCoinsViewModel
import com.shifthackz.aisdv1.presentation.widget.connectivity.ConnectivityViewModel
import com.shifthackz.aisdv1.presentation.widget.motd.MotdViewModel
import com.shifthackz.aisdv1.presentation.widget.version.VersionCheckerViewModel
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
    viewModelOf(::VersionCheckerViewModel)
    viewModelOf(::AvailableCoinsViewModel)
    viewModelOf(::MotdViewModel)
    viewModelOf(::InputHistoryViewModel)
    viewModelOf(::DebugMenuViewModel)

    viewModel { parameters ->
        val launchSource = ServerSetupLaunchSource.fromKey(parameters.get())
        val demoModeUrl = get<LinksProvider>().demoModeUrl
        val cloudUrl = get<LinksProvider>().cloudUrl
        ServerSetupViewModel(
            launchSource = launchSource,
            getConfigurationUseCase = get(),
            demoModeUrl = demoModeUrl,
            cloudUrl = cloudUrl,
            urlValidator = get(),
            stringValidator = get(),
            testConnectivityUseCase = get(),
            testHordeApiKeyUseCase = get(),
            setServerConfigurationUseCase = get(),
            dataPreLoaderUseCase = get(),
            schedulersProvider = get(),
            buildInfoProvider = get(),
            preferenceManager = get(),
            analytics = get(),
        )
    }

    viewModel { parameters ->
        GalleryDetailViewModel(parameters.get(), get(), get(), get(), get(), get(), get(), get())
    }
}
