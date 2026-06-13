package com.shifthackz.aisdv1.presentation.di

import com.shifthackz.aisdv1.presentation.core.AndroidGenerationPlatformServices
import com.shifthackz.aisdv1.presentation.core.GenerationFormUpdateEvent
import com.shifthackz.aisdv1.presentation.core.GenerationPlatformServices
import com.shifthackz.aisdv1.presentation.screen.debug.AndroidDebugMenuPlatformActions
import com.shifthackz.aisdv1.presentation.screen.debug.DebugMenuAccessor
import com.shifthackz.aisdv1.presentation.screen.debug.DebugMenuPlatformActions
import com.shifthackz.aisdv1.presentation.screen.gallery.detail.AndroidGalleryDetailPlatformActions
import com.shifthackz.aisdv1.presentation.screen.gallery.detail.GalleryDetailPlatformActions
import com.shifthackz.aisdv1.presentation.screen.gallery.list.AndroidGalleryPlatformActions
import com.shifthackz.aisdv1.presentation.screen.gallery.list.GalleryExportService
import com.shifthackz.aisdv1.presentation.screen.gallery.list.GalleryExporter
import com.shifthackz.aisdv1.presentation.screen.gallery.list.GalleryPlatformActions
import com.shifthackz.aisdv1.presentation.screen.logger.AndroidLogReader
import com.shifthackz.aisdv1.presentation.screen.logger.AndroidLoggerPlatformActions
import com.shifthackz.aisdv1.presentation.screen.logger.LogReader
import com.shifthackz.aisdv1.presentation.screen.logger.LoggerPlatformActions
import com.shifthackz.aisdv1.presentation.screen.setup.platform.AndroidServerSetupDownloadGuard
import com.shifthackz.aisdv1.presentation.screen.setup.platform.ServerSetupDownloadGuard
import com.shifthackz.aisdv1.presentation.screen.txt2img.AndroidImageSaver
import com.shifthackz.aisdv1.presentation.screen.txt2img.AndroidImageSharer
import com.shifthackz.aisdv1.presentation.screen.txt2img.ImageSaver
import com.shifthackz.aisdv1.presentation.screen.txt2img.ImageSharer
import com.shifthackz.aisdv1.presentation.widget.work.AndroidBackgroundWorkImageLoader
import com.shifthackz.aisdv1.presentation.widget.work.BackgroundWorkImageLoader
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val uiUtilsModule = module {
    factoryOf(::GalleryExporter) bind GalleryExportService::class
    factory { AndroidGalleryPlatformActions(androidContext(), get()) } bind GalleryPlatformActions::class
    factory {
        AndroidGalleryDetailPlatformActions(
            context = androidContext(),
            fileProviderDescriptor = get(),
            dispatchersProvider = get(),
            base64ToBitmapConverter = get(),
            imageSaver = get(),
        )
    } bind GalleryDetailPlatformActions::class
    factoryOf(::AndroidLogReader) bind LogReader::class
    factory { AndroidLoggerPlatformActions(androidContext()) } bind LoggerPlatformActions::class
    factoryOf(::AndroidDebugMenuPlatformActions) bind DebugMenuPlatformActions::class
    factoryOf(::AndroidBackgroundWorkImageLoader) bind BackgroundWorkImageLoader::class
    factoryOf(::AndroidServerSetupDownloadGuard) bind ServerSetupDownloadGuard::class
    factoryOf(::AndroidGenerationPlatformServices) bind GenerationPlatformServices::class
    single<ImageSaver> {
        AndroidImageSaver(
            mediaStoreGateway = get(),
            dispatchersProvider = get(),
        )
    }
    single<ImageSharer> {
        AndroidImageSharer(
            context = androidContext(),
            fileProviderDescriptor = get(),
            dispatchersProvider = get(),
            base64ToBitmapConverter = get(),
        )
    }
    singleOf(::GenerationFormUpdateEvent)
    singleOf(::DebugMenuAccessor)
}
