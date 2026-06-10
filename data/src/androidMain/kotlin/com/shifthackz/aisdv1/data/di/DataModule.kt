package com.shifthackz.aisdv1.data.di

import android.content.Context
import android.os.PowerManager
import com.shifthackz.aisdv1.data.gateway.DatabaseClearGatewayImpl
import com.shifthackz.aisdv1.data.gateway.mediastore.MediaStoreGatewayFactory
import com.shifthackz.aisdv1.data.local.AndroidDownloadableModelFileStore
import com.shifthackz.aisdv1.data.local.DownloadableModelFileStore
import com.shifthackz.aisdv1.data.remote.AndroidDownloadableModelFileDownloader
import com.shifthackz.aisdv1.data.remote.DownloadableModelFileDownloader
import com.shifthackz.aisdv1.data.repository.LocalDiffusionGenerationRepositoryImpl
import com.shifthackz.aisdv1.data.repository.MediaPipeGenerationRepositoryImpl
import com.shifthackz.aisdv1.data.repository.WakeLockRepositoryImpl
import com.shifthackz.aisdv1.domain.gateway.DatabaseClearGateway
import com.shifthackz.aisdv1.domain.gateway.MediaStoreGateway
import com.shifthackz.aisdv1.domain.repository.LocalDiffusionGenerationRepository
import com.shifthackz.aisdv1.domain.repository.MediaPipeGenerationRepository
import com.shifthackz.aisdv1.domain.repository.WakeLockRepository
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * Exposes the `androidDataOverridesModule` value used by the SDAI data layer.
 *
 * @author Dmitriy Moroz
 */
private val androidDataOverridesModule = module {
    singleOf(::DatabaseClearGatewayImpl) bind DatabaseClearGateway::class
    single<MediaStoreGateway> {
        MediaStoreGatewayFactory(androidContext(), get()).invoke()
    }
    single<WakeLockRepository> {
        WakeLockRepositoryImpl {
            androidContext().getSystemService(Context.POWER_SERVICE) as PowerManager
        }
    }

    factoryOf(::AndroidDownloadableModelFileDownloader) bind DownloadableModelFileDownloader::class
    factoryOf(::AndroidDownloadableModelFileStore) bind DownloadableModelFileStore::class
    factoryOf(::LocalDiffusionGenerationRepositoryImpl) bind LocalDiffusionGenerationRepository::class
    factoryOf(::MediaPipeGenerationRepositoryImpl) bind MediaPipeGenerationRepository::class
}

/**
 * Exposes the `dataModule` value used by the SDAI data layer.
 *
 * @author Dmitriy Moroz
 */
actual val dataModule = arrayOf(androidDataOverridesModule)
