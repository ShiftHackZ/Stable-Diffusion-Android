package com.shifthackz.aisdv1.storage.db_cache

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.shifthackz.aisdv1.storage.db_cache.CacheDatabase.Companion.DB_VERSION
import com.shifthackz.aisdv1.storage.converters.ListConverters
import com.shifthackz.aisdv1.storage.converters.MapConverters
import com.shifthackz.aisdv1.storage.db_cache.dao.ServerConfigurationDao
import com.shifthackz.aisdv1.storage.db_cache.dao.StableDiffusionModelDao
import com.shifthackz.aisdv1.storage.db_cache.dao.StableDiffusionSamplerDao
import com.shifthackz.aisdv1.storage.db_cache.entity.ServerConfigurationEntity
import com.shifthackz.aisdv1.storage.db_cache.entity.StableDiffusionModelEntity
import com.shifthackz.aisdv1.storage.db_cache.entity.StableDiffusionSamplerEntity

@Database(
    version = DB_VERSION,
    exportSchema = true,
    entities = [
        ServerConfigurationEntity::class,
        StableDiffusionModelEntity::class,
        StableDiffusionSamplerEntity::class,
    ],
)
@TypeConverters(
    MapConverters::class,
    ListConverters::class,
)
abstract class CacheDatabase : RoomDatabase() {
    abstract fun serverConfigurationDao(): ServerConfigurationDao
    abstract fun sdModelDao(): StableDiffusionModelDao
    abstract fun sdSamplerDao(): StableDiffusionSamplerDao

    companion object {
        const val DB_VERSION = 1
    }
}
