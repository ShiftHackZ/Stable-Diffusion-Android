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

/**
 * Coordinates `PersistentDatabase` behavior in the SDAI storage layer.
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
        const val DB_VERSION = 8
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
