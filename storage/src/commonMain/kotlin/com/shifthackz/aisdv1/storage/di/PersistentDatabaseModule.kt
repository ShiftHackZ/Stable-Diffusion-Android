package com.shifthackz.aisdv1.storage.di

import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.shifthackz.aisdv1.storage.db.persistent.PersistentDatabase
import kotlinx.coroutines.Dispatchers
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Platform-specific Room database builder binding.
 *
 * @author Dmitriy Moroz
 */
expect val persistentDatabasePlatformModule: Module

/**
 * Storage-layer Koin module that exposes the persistent database and DAO bindings.
 *
 * Network usage DAO is registered here so data repositories can observe traffic counters from Room.
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
    single { get<PersistentDatabase>().networkUsageDao() }
}

/**
 * Builds the Room database with the bundled SQLite driver and shared query dispatcher.
 *
 * @param builder Platform-specific Room database builder supplied by Koin.
 * @return Configured persistent database instance.
 *
 * @author Dmitriy Moroz
 */
internal fun getPersistentDatabase(
    builder: RoomDatabase.Builder<PersistentDatabase>,
): PersistentDatabase =
    builder
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.Default)
        .build()
