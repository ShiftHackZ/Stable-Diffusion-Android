package com.shifthackz.aisdv1.storage.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.shifthackz.aisdv1.storage.database.AppDatabase.Companion.DB_VERSION
import com.shifthackz.aisdv1.storage.database.converters.ListConverters
import com.shifthackz.aisdv1.storage.database.converters.MapConverters
import com.shifthackz.aisdv1.storage.database.dao.ServerConfigurationDao
import com.shifthackz.aisdv1.storage.database.dao.StableDiffusionModelDao
import com.shifthackz.aisdv1.storage.database.dao.StableDiffusionSamplerDao
import com.shifthackz.aisdv1.storage.database.entity.ServerConfigurationEntity
import com.shifthackz.aisdv1.storage.database.entity.StableDiffusionModelEntity
import com.shifthackz.aisdv1.storage.database.entity.StableDiffusionSamplerEntity

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
abstract class AppDatabase : RoomDatabase() {
    abstract fun serverConfigurationDao(): ServerConfigurationDao
    abstract fun sdModelDao(): StableDiffusionModelDao
    abstract fun sdSamplerDao(): StableDiffusionSamplerDao

    companion object {
        const val DB_NAME = "aisdv1_app_local_db"
        const val DB_VERSION = 1
    }
}
