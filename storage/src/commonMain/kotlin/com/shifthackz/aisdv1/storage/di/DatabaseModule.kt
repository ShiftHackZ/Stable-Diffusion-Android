package com.shifthackz.aisdv1.storage.di

import com.shifthackz.aisdv1.storage.db.cache.CacheDatabase
import com.shifthackz.aisdv1.storage.db.persistent.PersistentDatabase
import com.shifthackz.aisdv1.storage.gateway.GatewayClearCacheDb
import com.shifthackz.aisdv1.storage.gateway.GatewayClearPersistentDb
import org.koin.dsl.module

val databaseModule = module {
    includes(
        persistentDatabaseModule,
        cacheDatabaseModule,
    )

    single {
        GatewayClearCacheDb { clearCacheDatabase(get()) }
    }

    single {
        GatewayClearPersistentDb { clearPersistentDatabase(get()) }
    }
}

internal expect suspend fun clearCacheDatabase(database: CacheDatabase)

internal expect suspend fun clearPersistentDatabase(database: PersistentDatabase)
