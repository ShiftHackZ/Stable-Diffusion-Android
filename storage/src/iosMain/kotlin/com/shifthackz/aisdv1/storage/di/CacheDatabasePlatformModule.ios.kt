package com.shifthackz.aisdv1.storage.di

import androidx.room.Room
import androidx.room.RoomDatabase
import com.shifthackz.aisdv1.storage.db.cache.CacheDatabase
import org.koin.dsl.module

/**
 * Exposes the `cacheDatabasePlatformModule` value used by the SDAI storage layer.
 *
 * @author Dmitriy Moroz
 */
actual val cacheDatabasePlatformModule = module {
    single<RoomDatabase.Builder<CacheDatabase>>(cacheDatabaseBuilderQualifier) {
        Room.inMemoryDatabaseBuilder<CacheDatabase>()
    }
}
