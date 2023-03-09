package com.shifthackz.aisdv1.presentation.di

import com.shifthackz.aisdv1.presentation.screen.gallery.detail.GalleryDetailBitmapExporter
import com.shifthackz.aisdv1.presentation.screen.gallery.list.GalleryExporter
import com.shifthackz.aisdv1.presentation.screen.gallery.list.GallerySharing
import com.shifthackz.aisdv1.presentation.screen.settings.SettingsStateProducer
import org.koin.dsl.module

val presentationModule = (viewModelModule + module {

    factory { GalleryExporter(get(), get(), get(), get()) }

    factory { GalleryDetailBitmapExporter(get()) }

    factory { GallerySharing() }

    factory { SettingsStateProducer(get(), get()) }
}).toTypedArray()
