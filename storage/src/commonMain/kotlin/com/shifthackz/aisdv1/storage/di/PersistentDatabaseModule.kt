package com.shifthackz.aisdv1.storage.di

import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.shifthackz.aisdv1.storage.db.persistent.PersistentDatabase
import kotlinx.coroutines.Dispatchers
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Exposes the `persistentDatabasePlatformModule` value used by the SDAI storage layer.
 *
 * @author Dmitriy Moroz
 */
expect val persistentDatabasePlatformModule: Module

/**
 * Exposes the `persistentDatabaseModule` value used by the SDAI storage layer.
 *
 * @author Dmitriy Moroz
 */
val persistentDatabaseModule = module {
    includes(persistentDatabasePlatformModule)
    single {
        getPersistentDatabase(builder = get())
    }
    single { get<PersistentDatabase>().generationResultDao() }
    single { get<PersistentDatabase>().localModelDao() }
    single { get<PersistentDatabase>().huggingFaceModelDao() }
    single { get<PersistentDatabase>().supporterDao() }
    single { get<PersistentDatabase>().benchmarkResultDao() }
}

/**
 * Loads SDAI data through `getPersistentDatabase`.
 *
 * @param builder builder value consumed by the API.
 * @return Result produced by `getPersistentDatabase`.
 * @author Dmitriy Moroz
 */
internal fun getPersistentDatabase(
    builder: RoomDatabase.Builder<PersistentDatabase>,
): PersistentDatabase =
    builder
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.Default)
        .build()
