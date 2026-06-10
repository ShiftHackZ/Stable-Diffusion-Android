package com.shifthackz.aisdv1.storage.db.cache.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.shifthackz.aisdv1.storage.db.cache.contract.StableDiffusionEmbeddingContract
import com.shifthackz.aisdv1.storage.db.cache.entity.StableDiffusionEmbeddingEntity

@Dao
interface StableDiffusionEmbeddingDao {

    @Query("SELECT * FROM ${StableDiffusionEmbeddingContract.TABLE}")
    suspend fun queryAll(): List<StableDiffusionEmbeddingEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(items: List<StableDiffusionEmbeddingEntity>)

    @Query("DELETE FROM ${StableDiffusionEmbeddingContract.TABLE}")
    suspend fun deleteAll()
}
