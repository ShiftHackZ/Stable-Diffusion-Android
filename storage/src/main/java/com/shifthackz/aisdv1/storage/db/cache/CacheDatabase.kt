package com.shifthackz.aisdv1.storage.db.cache

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.shifthackz.aisdv1.storage.converters.ListConverters
import com.shifthackz.aisdv1.storage.converters.MapConverters
import com.shifthackz.aisdv1.storage.db.cache.CacheDatabase.Companion.DB_VERSION
import com.shifthackz.aisdv1.storage.db.cache.dao.ServerConfigurationDao
import com.shifthackz.aisdv1.storage.db.cache.dao.StableDiffusionEmbeddingDao
import com.shifthackz.aisdv1.storage.db.cache.dao.StableDiffusionHyperNetworkDao
import com.shifthackz.aisdv1.storage.db.cache.dao.StableDiffusionLoraDao
import com.shifthackz.aisdv1.storage.db.cache.dao.StableDiffusionModelDao
import com.shifthackz.aisdv1.storage.db.cache.dao.StableDiffusionSamplerDao
import com.shifthackz.aisdv1.storage.db.cache.dao.SwarmUiModelDao
import com.shifthackz.aisdv1.storage.db.cache.entity.ServerConfigurationEntity
import com.shifthackz.aisdv1.storage.db.cache.entity.StableDiffusionEmbeddingEntity
import com.shifthackz.aisdv1.storage.db.cache.entity.StableDiffusionHyperNetworkEntity
import com.shifthackz.aisdv1.storage.db.cache.entity.StableDiffusionLoraEntity
import com.shifthackz.aisdv1.storage.db.cache.entity.StableDiffusionModelEntity
import com.shifthackz.aisdv1.storage.db.cache.entity.StableDiffusionSamplerEntity
import com.shifthackz.aisdv1.storage.db.cache.entity.SwarmUiModelEntity

@Database(
    version = DB_VERSION,
    exportSchema = true,
    entities = [
        ServerConfigurationEntity::class,
        StableDiffusionModelEntity::class,
        StableDiffusionSamplerEntity::class,
        StableDiffusionLoraEntity::class,
        StableDiffusionHyperNetworkEntity::class,
        StableDiffusionEmbeddingEntity::class,
        SwarmUiModelEntity::class,
    ],
)
@TypeConverters(
    MapConverters::class,
    ListConverters::class,
)
internal abstract class CacheDatabase : RoomDatabase() {
    abstract fun serverConfigurationDao(): ServerConfigurationDao
    abstract fun sdModelDao(): StableDiffusionModelDao
    abstract fun sdSamplerDao(): StableDiffusionSamplerDao
    abstract fun sdLoraDao(): StableDiffusionLoraDao
    abstract fun sdHyperNetworkDao(): StableDiffusionHyperNetworkDao
    abstract fun sdEmbeddingDao(): StableDiffusionEmbeddingDao
    abstract fun swarmUiModelDao(): SwarmUiModelDao

    companion object {
        const val DB_VERSION = 1
    }
}
