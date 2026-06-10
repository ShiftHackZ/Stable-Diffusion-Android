package com.shifthackz.aisdv1.storage.di

import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.shifthackz.aisdv1.storage.db.persistent.PersistentDatabase
import kotlinx.coroutines.Dispatchers
import org.koin.core.module.Module
import org.koin.dsl.module

expect val persistentDatabasePlatformModule: Module

val persistentDatabaseModule = module {
    includes(persistentDatabasePlatformModule)
    single {
        getPersistentDatabase(builder = get())
    }
    single { get<PersistentDatabase>().generationResultDao() }
    single { get<PersistentDatabase>().localModelDao() }
    single { get<PersistentDatabase>().huggingFaceModelDao() }
    single { get<PersistentDatabase>().supporterDao() }
}

internal fun getPersistentDatabase(
    builder: RoomDatabase.Builder<PersistentDatabase>,
): PersistentDatabase =
    builder
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.Default)
        .build()
