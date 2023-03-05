package com.shifthackz.aisdv1.presentation.di

import com.shifthackz.aisdv1.presentation.screen.gallery.GalleryExporter
import org.koin.dsl.module

val presentationModule = (viewModelModule + module {

    factory { GalleryExporter(get(), get(), get(), get()) }
}).toTypedArray()
