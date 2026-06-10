package com.shifthackz.aisdv1.storage.di

import com.shifthackz.aisdv1.storage.db.cache.CacheDatabase
import com.shifthackz.aisdv1.storage.db.persistent.PersistentDatabase
import com.shifthackz.aisdv1.storage.gateway.GatewayClearCacheDb
import com.shifthackz.aisdv1.storage.gateway.GatewayClearPersistentDb
import org.koin.dsl.module

/**
 * Exposes the `databaseModule` value used by the SDAI storage layer.
 *
 * @author Dmitriy Moroz
 */
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

/**
 * Performs the SDAI side effect handled by `clearCacheDatabase`.
 *
 * @param database database value consumed by the API.
 * @author Dmitriy Moroz
 */
internal expect suspend fun clearCacheDatabase(database: CacheDatabase)

/**
 * Performs the SDAI side effect handled by `clearPersistentDatabase`.
 *
 * @param database database value consumed by the API.
 * @author Dmitriy Moroz
 */
internal expect suspend fun clearPersistentDatabase(database: PersistentDatabase)
