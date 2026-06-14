package com.shifthackz.aisdv1.storage.db.persistent

import androidx.room.AutoMigration
import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import com.shifthackz.aisdv1.storage.converters.ListConverters
import com.shifthackz.aisdv1.storage.db.persistent.PersistentDatabase.Companion.DB_VERSION
import com.shifthackz.aisdv1.storage.db.persistent.dao.BenchmarkResultDao
import com.shifthackz.aisdv1.storage.db.persistent.contract.GenerationResultContract
import com.shifthackz.aisdv1.storage.db.persistent.contract.LocalModelContract
import com.shifthackz.aisdv1.storage.db.persistent.dao.GenerationResultDao
import com.shifthackz.aisdv1.storage.db.persistent.dao.HuggingFaceModelDao
import com.shifthackz.aisdv1.storage.db.persistent.dao.LocalModelDao
import com.shifthackz.aisdv1.storage.db.persistent.dao.NetworkUsageDao
import com.shifthackz.aisdv1.storage.db.persistent.dao.SupporterDao
import com.shifthackz.aisdv1.storage.db.persistent.entity.BenchmarkResultEntity
import com.shifthackz.aisdv1.storage.db.persistent.entity.GenerationResultEntity
import com.shifthackz.aisdv1.storage.db.persistent.entity.HuggingFaceModelEntity
import com.shifthackz.aisdv1.storage.db.persistent.entity.LocalModelEntity
import com.shifthackz.aisdv1.storage.db.persistent.entity.NetworkUsageEntity
import com.shifthackz.aisdv1.storage.db.persistent.entity.SupporterEntity

/**
 * Main Room database containing generated images, downloaded models, and app statistics.
 *
 * Schema version 12 adds [NetworkUsageEntity] so traffic counters survive process restarts and can
 * be observed by the standalone network usage screen.
 *
 * @author Dmitriy Moroz
 */
@Database(
    version = DB_VERSION,
    exportSchema = true,
    entities = [
        GenerationResultEntity::class,
        LocalModelEntity::class,
        HuggingFaceModelEntity::class,
        SupporterEntity::class,
        BenchmarkResultEntity::class,
        NetworkUsageEntity::class,
    ],
    autoMigrations = [
        /**
         * Added 3 fields to [GenerationResultEntity]:
         * - [GenerationResultContract.SUB_SEED]
         * - [GenerationResultContract.SUB_SEED_STRENGTH]
         * - [GenerationResultContract.DENOISING_STRENGTH]
          *
          * @author Dmitriy Moroz
          */
        AutoMigration(from = 1, to = 2),
        /**
         * Added [LocalModelEntity].
          *
          * @author Dmitriy Moroz
          */
        AutoMigration(from = 2, to = 3),
        /**
         * Added [HuggingFaceModelEntity].
          *
          * @author Dmitriy Moroz
          */
        AutoMigration(from = 3, to = 4),
        /**
         * Added [SupporterEntity].
          *
          * @author Dmitriy Moroz
          */
        AutoMigration(from = 4, to = 5),
        /**
         * Added 1 field to [LocalModelEntity]:
         * - [LocalModelContract.TYPE]
          *
          * @author Dmitriy Moroz
          */
        AutoMigration(from = 5, to = 6),
        /**
         * Added 1 field to [GenerationResultEntity]:
         * - [GenerationResultContract.HIDDEN]
          *
          * @author Dmitriy Moroz
          */
        AutoMigration(from = 6, to = 7),
        /**
         * Added index for gallery queries:
         * - [GenerationResultContract.CREATED_AT_INDEX]
          *
          * @author Dmitriy Moroz
          */
        AutoMigration(from = 7, to = 8),
        /**
         * Added 1 field to [GenerationResultEntity]:
         * - [GenerationResultContract.MODEL_NAME]
          *
          * @author Dmitriy Moroz
          */
        AutoMigration(from = 8, to = 9),
        /**
         * Added 1 field to [GenerationResultEntity]:
         * - [GenerationResultContract.LIKED]
          *
          * @author Dmitriy Moroz
          */
        AutoMigration(from = 9, to = 10),
        /**
         * Added [BenchmarkResultEntity].
          *
          * @author Dmitriy Moroz
          */
        AutoMigration(from = 10, to = 11),
        /**
         * Added [NetworkUsageEntity].
          *
          * @author Dmitriy Moroz
          */
        AutoMigration(from = 11, to = 12),
    ],
)
@TypeConverters(
    ListConverters::class,
)
@ConstructedBy(PersistentDatabaseConstructor::class)
internal abstract class PersistentDatabase : RoomDatabase() {
    /**
     * Executes the `generationResultDao` step in the SDAI storage layer.
     *
     * @return Result produced by `generationResultDao`.
     * @author Dmitriy Moroz
     */
    abstract fun generationResultDao(): GenerationResultDao
    /**
     * Executes the `localModelDao` step in the SDAI storage layer.
     *
     * @return Result produced by `localModelDao`.
     * @author Dmitriy Moroz
     */
    abstract fun localModelDao(): LocalModelDao
    /**
     * Executes the `huggingFaceModelDao` step in the SDAI storage layer.
     *
     * @return Result produced by `huggingFaceModelDao`.
     * @author Dmitriy Moroz
     */
    abstract fun huggingFaceModelDao(): HuggingFaceModelDao
    /**
     * Executes the `supporterDao` step in the SDAI storage layer.
     *
     * @return Result produced by `supporterDao`.
     * @author Dmitriy Moroz
     */
    abstract fun supporterDao(): SupporterDao
    /**
     * Executes the `benchmarkResultDao` step in the SDAI storage layer.
     *
     * @return Result produced by `benchmarkResultDao`.
     * @author Dmitriy Moroz
     */
    abstract fun benchmarkResultDao(): BenchmarkResultDao
    /**
     * Returns the DAO used to persist and observe network traffic counters.
     *
     * @author Dmitriy Moroz
     */
    abstract fun networkUsageDao(): NetworkUsageDao

    /**
     * Provides the `companion object` singleton used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    companion object {
        /**
         * Exposes the `DB_NAME` value used by the SDAI storage layer.
         *
         * @author Dmitriy Moroz
         */
        const val DB_NAME = "ai_sd_v1_storage_db"
        /**
         * Exposes the `DB_VERSION` value used by the SDAI storage layer.
         *
         * @author Dmitriy Moroz
         */
        const val DB_VERSION = 12
    }
}

/**
 * Provides the `PersistentDatabaseConstructor` singleton used by the SDAI storage layer.
 *
 * @author Dmitriy Moroz
 */
@Suppress("KotlinNoActualForExpect")
internal expect object PersistentDatabaseConstructor : RoomDatabaseConstructor<PersistentDatabase> {
    /**
     * Executes the `initialize` step in the SDAI storage layer.
     *
     * @return Result produced by `initialize`.
     * @author Dmitriy Moroz
     */
    override fun initialize(): PersistentDatabase
}
