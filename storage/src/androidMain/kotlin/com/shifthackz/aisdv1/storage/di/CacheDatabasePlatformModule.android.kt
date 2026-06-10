package com.shifthackz.aisdv1.storage.di

import androidx.room.Room
import androidx.room.RoomDatabase
import com.shifthackz.aisdv1.storage.db.cache.CacheDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual val cacheDatabasePlatformModule = module {
    single<RoomDatabase.Builder<CacheDatabase>>(cacheDatabaseBuilderQualifier) {
        Room.inMemoryDatabaseBuilder<CacheDatabase>(
            context = androidContext().applicationContext,
        )
    }
}
