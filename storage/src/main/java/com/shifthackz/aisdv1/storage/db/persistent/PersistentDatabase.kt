package com.shifthackz.aisdv1.storage.db.persistent

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.shifthackz.aisdv1.storage.converters.*
import com.shifthackz.aisdv1.storage.db.persistent.PersistentDatabase.Companion.DB_VERSION
import com.shifthackz.aisdv1.storage.db.persistent.contract.*
import com.shifthackz.aisdv1.storage.db.persistent.dao.*
import com.shifthackz.aisdv1.storage.db.persistent.entity.*

@Database(
    version = DB_VERSION,
    exportSchema = true,
    entities = [
        GenerationResultEntity::class,
        LocalModelEntity::class,
        HuggingFaceModelEntity::class,
        SupporterEntity::class,
    ],
    autoMigrations = [
        /**
         * Added 3 fields to [GenerationResultEntity]:
         * - [GenerationResultContract.SUB_SEED]
         * - [GenerationResultContract.SUB_SEED_STRENGTH]
         * - [GenerationResultContract.DENOISING_STRENGTH]
         */
        AutoMigration(from = 1, to = 2),
        /**
         * Added [LocalModelEntity].
         */
        AutoMigration(from = 2, to = 3),
        /**
         * Added [HuggingFaceModelEntity].
         */
        AutoMigration(from = 3, to = 4),
        /**
         * Added [SupporterEntity].
         */
        AutoMigration(from = 4, to = 5),
        /**
         * Added 1 field to [LocalModelEntity]:
         * - [LocalModelContract.TYPE]
         */
        AutoMigration(from = 5, to = 6),
        /**
         * Added 1 field to [GenerationResultEntity]:
         * - [GenerationResultContract.HIDDEN]
         */
        AutoMigration(from = 6, to = 7),
    ],
)
@TypeConverters(
    DateConverters::class,
    ListConverters::class,
)
internal abstract class PersistentDatabase : RoomDatabase() {
    abstract fun generationResultDao(): GenerationResultDao
    abstract fun localModelDao(): LocalModelDao
    abstract fun huggingFaceModelDao(): HuggingFaceModelDao
    abstract fun supporterDao(): SupporterDao

    companion object {
        const val DB_NAME = "ai_sd_v1_storage_db"
        const val DB_VERSION = 7
    }
}
