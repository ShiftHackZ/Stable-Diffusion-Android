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
    database.serverConfigurationDao().deleteAll()
    database.sdModelDao().deleteAll()
    database.sdSamplerDao().deleteAll()
    database.sdLoraDao().deleteAll()
    database.sdHyperNetworkDao().deleteAll()
    database.sdEmbeddingDao().deleteAll()
    database.swarmUiModelDao().deleteAll()
}

/**
 * Performs the SDAI side effect handled by `clearPersistentDatabase`.
 *
 * @param database database value consumed by the API.
 * @author Dmitriy Moroz
 */
internal actual suspend fun clearPersistentDatabase(database: PersistentDatabase) {
    database.generationResultDao().deleteAll()
    database.localModelDao().deleteAll()
    database.huggingFaceModelDao().deleteAll()
    database.supporterDao().deleteAll()
}
