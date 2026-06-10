package com.shifthackz.aisdv1.storage.di

import com.shifthackz.aisdv1.storage.db.cache.CacheDatabase
import com.shifthackz.aisdv1.storage.db.persistent.PersistentDatabase

internal actual suspend fun clearCacheDatabase(database: CacheDatabase) {
    database.serverConfigurationDao().deleteAll()
    database.sdModelDao().deleteAll()
    database.sdSamplerDao().deleteAll()
    database.sdLoraDao().deleteAll()
    database.sdHyperNetworkDao().deleteAll()
    database.sdEmbeddingDao().deleteAll()
    database.swarmUiModelDao().deleteAll()
}

internal actual suspend fun clearPersistentDatabase(database: PersistentDatabase) {
    database.generationResultDao().deleteAll()
    database.localModelDao().deleteAll()
    database.huggingFaceModelDao().deleteAll()
    database.supporterDao().deleteAll()
}
