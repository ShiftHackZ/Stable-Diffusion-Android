package com.shifthackz.aisdv1.storage.di

import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.shifthackz.aisdv1.storage.db.cache.CacheDatabase
import kotlinx.coroutines.Dispatchers
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

internal val cacheDatabaseBuilderQualifier = named("cacheDatabaseBuilder")

expect val cacheDatabasePlatformModule: Module

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
}

internal fun getCacheDatabase(
    builder: RoomDatabase.Builder<CacheDatabase>,
): CacheDatabase =
    builder
        .setDriver(BundledSQLiteDriver())
        .fallbackToDestructiveMigration(dropAllTables = true)
        .setQueryCoroutineContext(Dispatchers.Default)
        .build()
