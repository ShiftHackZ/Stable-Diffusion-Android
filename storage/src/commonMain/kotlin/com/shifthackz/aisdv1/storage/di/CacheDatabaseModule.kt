package com.shifthackz.aisdv1.storage.di

import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.shifthackz.aisdv1.storage.db.cache.CacheDatabase
import kotlinx.coroutines.Dispatchers
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 * Exposes the `cacheDatabaseBuilderQualifier` value used by the SDAI storage layer.
 *
 * @author Dmitriy Moroz
 */
internal val cacheDatabaseBuilderQualifier = named("cacheDatabaseBuilder")

/**
 * Exposes the `cacheDatabasePlatformModule` value used by the SDAI storage layer.
 *
 * @author Dmitriy Moroz
 */
expect val cacheDatabasePlatformModule: Module

/**
 * Exposes the `cacheDatabaseModule` value used by the SDAI storage layer.
 *
 * @author Dmitriy Moroz
 */
val cacheDatabaseModule = module {
    includes(cacheDatabasePlatformModule)

    single {
        getCacheDatabase(builder = get(cacheDatabaseBuilderQualifier))
    }

    single { get<CacheDatabase>().sdModelDao() }
    single { get<CacheDatabase>().sdSamplerDao() }
    single { get<CacheDatabase>().sdLoraDao() }
    single { get<CacheDatabase>().sdHyperNetworkDao() }
    single { get<CacheDatabase>().sdEmbeddingDao() }
    single { get<CacheDatabase>().serverConfigurationDao() }
    single { get<CacheDatabase>().swarmUiModelDao() }
    single { get<CacheDatabase>().arliAiModelDao() }
}

/**
 * Loads SDAI data through `getCacheDatabase`.
 *
 * @param builder builder value consumed by the API.
 * @return Result produced by `getCacheDatabase`.
 * @author Dmitriy Moroz
 */
internal fun getCacheDatabase(
    builder: RoomDatabase.Builder<CacheDatabase>,
): CacheDatabase =
    builder
        .setDriver(BundledSQLiteDriver())
        .fallbackToDestructiveMigration(dropAllTables = true)
        .setQueryCoroutineContext(Dispatchers.Default)
        .build()
