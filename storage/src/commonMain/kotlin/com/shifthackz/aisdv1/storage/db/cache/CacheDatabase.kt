package com.shifthackz.aisdv1.storage.db.cache

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import com.shifthackz.aisdv1.storage.converters.ListConverters
import com.shifthackz.aisdv1.storage.converters.MapConverters
import com.shifthackz.aisdv1.storage.db.cache.CacheDatabase.Companion.DB_VERSION
import com.shifthackz.aisdv1.storage.db.cache.dao.ArliAiModelDao
import com.shifthackz.aisdv1.storage.db.cache.dao.ServerConfigurationDao
import com.shifthackz.aisdv1.storage.db.cache.dao.StableDiffusionEmbeddingDao
import com.shifthackz.aisdv1.storage.db.cache.dao.StableDiffusionHyperNetworkDao
import com.shifthackz.aisdv1.storage.db.cache.dao.StableDiffusionLoraDao
import com.shifthackz.aisdv1.storage.db.cache.dao.StableDiffusionModelDao
import com.shifthackz.aisdv1.storage.db.cache.dao.StableDiffusionSamplerDao
import com.shifthackz.aisdv1.storage.db.cache.dao.SwarmUiModelDao
import com.shifthackz.aisdv1.storage.db.cache.entity.ArliAiModelEntity
import com.shifthackz.aisdv1.storage.db.cache.entity.ServerConfigurationEntity
import com.shifthackz.aisdv1.storage.db.cache.entity.StableDiffusionEmbeddingEntity
import com.shifthackz.aisdv1.storage.db.cache.entity.StableDiffusionHyperNetworkEntity
import com.shifthackz.aisdv1.storage.db.cache.entity.StableDiffusionLoraEntity
import com.shifthackz.aisdv1.storage.db.cache.entity.StableDiffusionModelEntity
import com.shifthackz.aisdv1.storage.db.cache.entity.StableDiffusionSamplerEntity
import com.shifthackz.aisdv1.storage.db.cache.entity.SwarmUiModelEntity

/**
 * Coordinates `CacheDatabase` behavior in the SDAI storage layer.
 *
 * @author Dmitriy Moroz
 */
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
        ArliAiModelEntity::class,
    ],
)
@TypeConverters(
    MapConverters::class,
    ListConverters::class,
)
@ConstructedBy(CacheDatabaseConstructor::class)
internal abstract class CacheDatabase : RoomDatabase() {
    /**
     * Executes the `serverConfigurationDao` step in the SDAI storage layer.
     *
     * @return Result produced by `serverConfigurationDao`.
     * @author Dmitriy Moroz
     */
    abstract fun serverConfigurationDao(): ServerConfigurationDao
    /**
     * Executes the `sdModelDao` step in the SDAI storage layer.
     *
     * @return Result produced by `sdModelDao`.
     * @author Dmitriy Moroz
     */
    abstract fun sdModelDao(): StableDiffusionModelDao
    /**
     * Executes the `sdSamplerDao` step in the SDAI storage layer.
     *
     * @return Result produced by `sdSamplerDao`.
     * @author Dmitriy Moroz
     */
    abstract fun sdSamplerDao(): StableDiffusionSamplerDao
    /**
     * Executes the `sdLoraDao` step in the SDAI storage layer.
     *
     * @return Result produced by `sdLoraDao`.
     * @author Dmitriy Moroz
     */
    abstract fun sdLoraDao(): StableDiffusionLoraDao
    /**
     * Executes the `sdHyperNetworkDao` step in the SDAI storage layer.
     *
     * @return Result produced by `sdHyperNetworkDao`.
     * @author Dmitriy Moroz
     */
    abstract fun sdHyperNetworkDao(): StableDiffusionHyperNetworkDao
    /**
     * Executes the `sdEmbeddingDao` step in the SDAI storage layer.
     *
     * @return Result produced by `sdEmbeddingDao`.
     * @author Dmitriy Moroz
     */
    abstract fun sdEmbeddingDao(): StableDiffusionEmbeddingDao
    /**
     * Executes the `swarmUiModelDao` step in the SDAI storage layer.
     *
     * @return Result produced by `swarmUiModelDao`.
     * @author Dmitriy Moroz
     */
    abstract fun swarmUiModelDao(): SwarmUiModelDao
    /**
     * Executes the `arliAiModelDao` step in the SDAI storage layer.
     *
     * @return Result produced by `arliAiModelDao`.
     * @author Dmitriy Moroz
     */
    abstract fun arliAiModelDao(): ArliAiModelDao

    /**
     * Provides the `companion object` singleton used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    companion object {
        /**
         * Exposes the `DB_VERSION` value used by the SDAI storage layer.
         *
         * @author Dmitriy Moroz
         */
        const val DB_VERSION = 1
    }
}

/**
 * Provides the `CacheDatabaseConstructor` singleton used by the SDAI storage layer.
 *
 * @author Dmitriy Moroz
 */
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
internal expect object CacheDatabaseConstructor : RoomDatabaseConstructor<CacheDatabase> {
    /**
     * Executes the `initialize` step in the SDAI storage layer.
     *
     * @return Result produced by `initialize`.
     * @author Dmitriy Moroz
     */
    override fun initialize(): CacheDatabase
}
