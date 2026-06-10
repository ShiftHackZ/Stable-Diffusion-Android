package com.shifthackz.aisdv1.storage.db.cache.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.shifthackz.aisdv1.storage.db.cache.contract.StableDiffusionSamplerContract
import com.shifthackz.aisdv1.storage.db.cache.entity.StableDiffusionSamplerEntity

@Dao
interface StableDiffusionSamplerDao {

    @Query("SELECT * FROM ${StableDiffusionSamplerContract.TABLE}")
    suspend fun queryAll(): List<StableDiffusionSamplerEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(items: List<StableDiffusionSamplerEntity>)

    @Query("DELETE FROM ${StableDiffusionSamplerContract.TABLE}")
    suspend fun deleteAll()
}
