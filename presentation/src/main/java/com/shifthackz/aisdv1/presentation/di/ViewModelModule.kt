package com.shifthackz.aisdv1.presentation.di

import com.shifthackz.aisdv1.core.common.links.LinksProvider
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
import com.shifthackz.aisdv1.presentation.widget.version.VersionCheckerViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::SplashViewModel)
    viewModelOf(::ServerSetupViewModel)
    viewModelOf(::HomeNavigationViewModel)
    viewModelOf(::ConfigurationLoaderViewModel)
    viewModelOf(::ImageToImageViewModel)
    viewModelOf(::TextToImageViewModel)
    viewModelOf(::SettingsViewModel)
    viewModelOf(::GalleryViewModel)
    viewModelOf(::ConnectivityViewModel)
    viewModelOf(::VersionCheckerViewModel)

    viewModel { parameters ->
        val launchSource = ServerSetupLaunchSource.fromKey(parameters.get())
        val demoModeUrl = get<LinksProvider>().demoModeUrl
        ServerSetupViewModel(launchSource, get(), demoModeUrl, get(), get(), get(), get(), get(), get())
    }

    viewModel { parameters ->
        GalleryDetailViewModel(parameters.get(), get(), get(), get(), get(), get(), get())
    }
}
