package com.shifthackz.aisdv1.storage.db.cache.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.shifthackz.aisdv1.storage.db.cache.contract.StableDiffusionEmbeddingContract
import com.shifthackz.aisdv1.storage.db.cache.entity.StableDiffusionEmbeddingEntity

/**
 * Defines the `StableDiffusionEmbeddingDao` contract for the SDAI storage layer.
 *
 * @author Dmitriy Moroz
 */
@Dao
interface StableDiffusionEmbeddingDao {

    /**
     * Executes the `queryAll` step in the SDAI storage layer.
     *
     * @return Result produced by `queryAll`.
     * @author Dmitriy Moroz
     */
    @Query("SELECT * FROM ${StableDiffusionEmbeddingContract.TABLE}")
    suspend fun queryAll(): List<StableDiffusionEmbeddingEntity>

    /**
     * Performs the SDAI side effect handled by `insertList`.
     *
     * @param items items value consumed by the API.
     * @author Dmitriy Moroz
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(items: List<StableDiffusionEmbeddingEntity>)

    /**
     * Performs the SDAI side effect handled by `deleteAll`.
     *
     * @author Dmitriy Moroz
     */
    @Query("DELETE FROM ${StableDiffusionEmbeddingContract.TABLE}")
    suspend fun deleteAll()
}
