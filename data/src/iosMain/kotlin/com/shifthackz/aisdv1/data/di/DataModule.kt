package com.shifthackz.aisdv1.data.di

import com.shifthackz.aisdv1.core.common.file.FileProviderDescriptor
import com.shifthackz.aisdv1.data.gateway.mediastore.IosMediaStoreGateway
import com.shifthackz.aisdv1.data.local.DownloadableModelFileStore
import com.shifthackz.aisdv1.data.local.IosDownloadableModelFileStore
import com.shifthackz.aisdv1.data.remote.DownloadableModelFileDownloader
import com.shifthackz.aisdv1.data.remote.IosDownloadableModelFileDownloader
import com.shifthackz.aisdv1.data.repository.IosWakeLockRepository
import com.shifthackz.aisdv1.domain.gateway.MediaStoreGateway
import com.shifthackz.aisdv1.domain.repository.WakeLockRepository
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module
import platform.Foundation.NSBundle
import platform.Foundation.NSHomeDirectory

/**
 * Exposes the `iosDataOverridesModule` value used by the SDAI data layer.
 *
 * @author Dmitriy Moroz
 */
private val iosDataOverridesModule = module {
    single<FileProviderDescriptor> {
        IosFileProviderDescriptor()
    }
    single<MediaStoreGateway> {
        IosMediaStoreGateway()
    }
    single<WakeLockRepository> {
        IosWakeLockRepository()
    }
    factoryOf(::IosDownloadableModelFileDownloader) bind DownloadableModelFileDownloader::class
    factoryOf(::IosDownloadableModelFileStore) bind DownloadableModelFileStore::class
}

/**
 * Exposes the `dataModule` value used by the SDAI data layer.
 *
 * @author Dmitriy Moroz
 */
actual val dataModule = arrayOf(iosDataOverridesModule)

private class IosFileProviderDescriptor : FileProviderDescriptor {

    private val homePath = NSHomeDirectory()
    private val cachePath = "$homePath/Library/Caches"

    override val providerPath: String =
        NSBundle.mainBundle.bundleIdentifier ?: "com.shifthackz.aisdv1.app"

    override val imagesCacheDirPath: String = "$cachePath/images"

    override val logsCacheDirPath: String = "$cachePath/logs"

    override val localModelDirPath: String = "$homePath/Library/Application Support/model"

    override val workCacheDirPath: String = "$cachePath/work"
}
