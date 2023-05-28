package com.shifthackz.aisdv1.storage.db.persistent

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.shifthackz.aisdv1.storage.converters.DateConverters
import com.shifthackz.aisdv1.storage.db.persistent.PersistentDatabase.Companion.DB_VERSION
import com.shifthackz.aisdv1.storage.db.persistent.contract.GenerationResultContract
import com.shifthackz.aisdv1.storage.db.persistent.dao.GenerationResultDao
import com.shifthackz.aisdv1.storage.db.persistent.entity.GenerationResultEntity

@Database(
    version = DB_VERSION,
    exportSchema = true,
    entities = [
        GenerationResultEntity::class,
    ],
    autoMigrations = [
        /**
         * Added 3 fields to [GenerationResultEntity]:
         * - [GenerationResultContract.SUB_SEED]
         * - [GenerationResultContract.SUB_SEED_STRENGTH]
         * - [GenerationResultContract.DENOISING_STRENGTH]
         */
        AutoMigration(from = 1, to = 2),
    ],
)
@TypeConverters(DateConverters::class)
internal abstract class PersistentDatabase : RoomDatabase() {
    abstract fun generationResultDao(): GenerationResultDao

    companion object {
        const val DB_NAME = "ai_sd_v1_storage_db"
        const val DB_VERSION = 2
    }
}
