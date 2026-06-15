package com.shifthackz.aisdv1.storage.db.cache.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.shifthackz.aisdv1.storage.db.cache.contract.ArliAiModelContract
import com.shifthackz.aisdv1.storage.db.cache.entity.ArliAiModelEntity

/**
 * Provides Room access to cached ArliAI checkpoint metadata.
 *
 * @author Dmitriy Moroz
 */
@Dao
interface ArliAiModelDao {

    /**
     * Reads all cached ArliAI checkpoints.
     *
     * @return rows currently stored in the ArliAI model cache table.
     *
     * @author Dmitriy Moroz
     */
    @Query("SELECT * FROM ${ArliAiModelContract.TABLE}")
    suspend fun queryAll(): List<ArliAiModelEntity>

    /**
     * Inserts or replaces cached ArliAI checkpoints.
     *
     * @param items rows produced from the latest provider model list.
     *
     * @author Dmitriy Moroz
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(items: List<ArliAiModelEntity>)

    /**
     * Clears all cached ArliAI checkpoints before a refresh writes the new list.
     *
     * @author Dmitriy Moroz
     */
    @Query("DELETE FROM ${ArliAiModelContract.TABLE}")
    suspend fun deleteAll()
}
