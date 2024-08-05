package com.shifthackz.aisdv1.storage.di

import androidx.room.Room
import com.shifthackz.aisdv1.storage.db.cache.CacheDatabase
import com.shifthackz.aisdv1.storage.db.persistent.PersistentDatabase
import com.shifthackz.aisdv1.storage.gateway.GatewayClearCacheDb
import com.shifthackz.aisdv1.storage.gateway.GatewayClearPersistentDb
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
            .build()
    }
    //endregion

    //region GATEWAYS
    single {
        GatewayClearCacheDb { get<CacheDatabase>().clearAllTables() }
    }

    single {
        GatewayClearPersistentDb { get<PersistentDatabase>().clearAllTables() }
    }
    //endregion

    //region CACHE DB DAOs
    single { get<CacheDatabase>().sdModelDao() }
    single { get<CacheDatabase>().sdSamplerDao() }
    single { get<CacheDatabase>().sdLoraDao() }
    single { get<CacheDatabase>().sdHyperNetworkDao() }
    single { get<CacheDatabase>().sdEmbeddingDao() }
    single { get<CacheDatabase>().serverConfigurationDao() }
    single { get<CacheDatabase>().swarmUiModelDao() }
    //endregion

    //region PERSISTENT DB DAOs
    single { get<PersistentDatabase>().generationResultDao() }
    single { get<PersistentDatabase>().localModelDao() }
    single { get<PersistentDatabase>().huggingFaceModelDao() }
    single { get<PersistentDatabase>().supporterDao() }
    //endregion
}
