package com.shifthackz.aisdv1.presentation.di

import com.shifthackz.aisdv1.presentation.screen.gallery.list.GalleryExporter
import com.shifthackz.aisdv1.presentation.screen.gallery.list.GallerySharing
import org.koin.dsl.module

val presentationModule = (viewModelModule + module {

    factory { GalleryExporter(get(), get(), get(), get()) }

    factory { GallerySharing() }
}).toTypedArray()
