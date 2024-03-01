package com.shifthackz.aisdv1.presentation.di

import androidx.core.app.NotificationManagerCompat
import com.shifthackz.aisdv1.presentation.notification.SdaiPushNotificationManager
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import com.shifthackz.aisdv1.presentation.core.GenerationFormUpdateEvent
import com.shifthackz.aisdv1.presentation.screen.debug.DebugMenuAccessor
import com.shifthackz.aisdv1.presentation.screen.gallery.detail.GalleryDetailBitmapExporter
import com.shifthackz.aisdv1.presentation.screen.gallery.detail.GalleryDetailSharing
import com.shifthackz.aisdv1.presentation.screen.gallery.list.GalleryExporter
import com.shifthackz.aisdv1.presentation.screen.settings.SettingsStateProducer

internal val uiUtilsModule = module {
    factory { NotificationManagerCompat.from(androidContext()) }
    factory { SdaiPushNotificationManager(androidContext(), get()) }
    factoryOf(::GalleryExporter)
    factoryOf(::GalleryDetailBitmapExporter)
    factoryOf(::GalleryDetailSharing)
    factoryOf(::SettingsStateProducer)
    singleOf(::GenerationFormUpdateEvent)
    singleOf(::DebugMenuAccessor)
}
