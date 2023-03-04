package com.shifthackz.aisdv1.storage.di

import androidx.room.Room
import com.shifthackz.aisdv1.storage.db_cache.CacheDatabase
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val databaseModule = module {

    single {
        //Room.databaseBuilder(androidApplication(), AppDatabase::class.java, AppDatabase.DB_NAME)
        Room.inMemoryDatabaseBuilder(androidApplication(), CacheDatabase::class.java)
            .fallbackToDestructiveMigration()
            .build()
    }

    single { get<CacheDatabase>().sdModelDao() }
    single { get<CacheDatabase>().sdSamplerDao() }
    single { get<CacheDatabase>().serverConfigurationDao() }
}
