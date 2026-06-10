package com.shifthackz.aisdv1.storage.db.persistent

import androidx.room.AutoMigration
import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import com.shifthackz.aisdv1.storage.converters.ListConverters
import com.shifthackz.aisdv1.storage.db.persistent.PersistentDatabase.Companion.DB_VERSION
import com.shifthackz.aisdv1.storage.db.persistent.contract.GenerationResultContract
import com.shifthackz.aisdv1.storage.db.persistent.contract.LocalModelContract
import com.shifthackz.aisdv1.storage.db.persistent.dao.GenerationResultDao
import com.shifthackz.aisdv1.storage.db.persistent.dao.HuggingFaceModelDao
import com.shifthackz.aisdv1.storage.db.persistent.dao.LocalModelDao
import com.shifthackz.aisdv1.storage.db.persistent.dao.SupporterDao
import com.shifthackz.aisdv1.storage.db.persistent.entity.GenerationResultEntity
import com.shifthackz.aisdv1.storage.db.persistent.entity.HuggingFaceModelEntity
import com.shifthackz.aisdv1.storage.db.persistent.entity.LocalModelEntity
import com.shifthackz.aisdv1.storage.db.persistent.entity.SupporterEntity

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
        /**
         * Added index for gallery queries:
         * - [GenerationResultContract.CREATED_AT_INDEX]
         */
        AutoMigration(from = 7, to = 8),
    ],
)
@TypeConverters(
    ListConverters::class,
)
@ConstructedBy(PersistentDatabaseConstructor::class)
internal abstract class PersistentDatabase : RoomDatabase() {
    abstract fun generationResultDao(): GenerationResultDao
    abstract fun localModelDao(): LocalModelDao
    abstract fun huggingFaceModelDao(): HuggingFaceModelDao
    abstract fun supporterDao(): SupporterDao

    companion object {
        const val DB_NAME = "ai_sd_v1_storage_db"
        const val DB_VERSION = 8
    }
}

@Suppress("KotlinNoActualForExpect")
internal expect object PersistentDatabaseConstructor : RoomDatabaseConstructor<PersistentDatabase> {
    override fun initialize(): PersistentDatabase
}
