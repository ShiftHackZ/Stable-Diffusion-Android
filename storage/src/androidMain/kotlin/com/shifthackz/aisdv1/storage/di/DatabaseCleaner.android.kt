package com.shifthackz.aisdv1.storage.di

import com.shifthackz.aisdv1.storage.db.cache.CacheDatabase
import com.shifthackz.aisdv1.storage.db.persistent.PersistentDatabase

/**
 * Performs the SDAI side effect handled by `clearCacheDatabase`.
 *
 * @param database database value consumed by the API.
 * @author Dmitriy Moroz
 */
internal actual suspend fun clearCacheDatabase(database: CacheDatabase) {
    database.clearAllTables()
}

/**
 * Performs the SDAI side effect handled by `clearPersistentDatabase`.
 *
 * @param database database value consumed by the API.
 * @author Dmitriy Moroz
 */
internal actual suspend fun clearPersistentDatabase(database: PersistentDatabase) {
    database.clearAllTables()
}
