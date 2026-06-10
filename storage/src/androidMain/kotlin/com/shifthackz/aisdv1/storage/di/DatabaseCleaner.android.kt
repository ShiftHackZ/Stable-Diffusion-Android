package com.shifthackz.aisdv1.storage.di

import com.shifthackz.aisdv1.storage.db.cache.CacheDatabase
import com.shifthackz.aisdv1.storage.db.persistent.PersistentDatabase

internal actual suspend fun clearCacheDatabase(database: CacheDatabase) {
    database.clearAllTables()
}

internal actual suspend fun clearPersistentDatabase(database: PersistentDatabase) {
    database.clearAllTables()
}
