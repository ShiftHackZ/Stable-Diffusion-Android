package com.shifthackz.aisdv1.storage.di

import androidx.room.Room
import com.shifthackz.aisdv1.storage.db.cache.CacheDatabase
import com.shifthackz.aisdv1.storage.db.persistent.PersistentDatabase
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val databaseModule = module {
    //region DATABASES
    single {
        Room.inMemoryDatabaseBuilder(androidApplication(), CacheDatabase::class.java)
            .fallbackToDestructiveMigration()
            .build()
    }

    single {
        Room.databaseBuilder(
            androidApplication(),
            PersistentDatabase::class.java,
            PersistentDatabase.DB_NAME,
        )
            .fallbackToDestructiveMigration()
            .build()
    }
    //endregion

    //region CACHE DB DAOs
    single { get<CacheDatabase>().sdModelDao() }
    single { get<CacheDatabase>().sdSamplerDao() }
    single { get<CacheDatabase>().serverConfigurationDao() }
    //endregion

    //region PERSISTENT DB DAOs
    single { get<PersistentDatabase>().generationResultDao() }
    //endregion
}
