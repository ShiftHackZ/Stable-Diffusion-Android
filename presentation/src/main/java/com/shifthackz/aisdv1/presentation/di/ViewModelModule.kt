package com.shifthackz.aisdv1.presentation.di

import com.shifthackz.aisdv1.presentation.screen.gallery.detail.GalleryDetailViewModel
import com.shifthackz.aisdv1.presentation.screen.gallery.list.GalleryViewModel
import com.shifthackz.aisdv1.presentation.screen.splash.SplashLoaderViewModel
import com.shifthackz.aisdv1.presentation.screen.txt2img.TextToImageViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { SplashLoaderViewModel(get(), get()) }
    viewModel { TextToImageViewModel(get(), get(), get()) }
    viewModel { GalleryViewModel(get(), get(), get(), get()) }
    viewModel { parameters ->
        GalleryDetailViewModel(parameters.get(), get(), get(), get())
    }
}
