package com.shifthackz.aisdv1.presentation.di

import com.shifthackz.aisdv1.presentation.core.GenerationFormUpdateEvent
import com.shifthackz.aisdv1.presentation.screen.debug.DebugMenuAccessor
import com.shifthackz.aisdv1.presentation.screen.gallery.detail.GalleryDetailBitmapExporter
import com.shifthackz.aisdv1.presentation.screen.gallery.detail.GalleryDetailSharing
import com.shifthackz.aisdv1.presentation.screen.gallery.list.GalleryExporter
import com.shifthackz.aisdv1.presentation.screen.inpaint.InPaintStateProducer
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal val uiUtilsModule = module {
    factoryOf(::GalleryExporter)
    factoryOf(::GalleryDetailBitmapExporter)
    factoryOf(::GalleryDetailSharing)
    singleOf(::GenerationFormUpdateEvent)
    singleOf(::DebugMenuAccessor)
    singleOf(::InPaintStateProducer)
}
