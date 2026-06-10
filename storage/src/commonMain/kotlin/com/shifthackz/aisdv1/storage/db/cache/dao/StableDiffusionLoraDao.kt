package com.shifthackz.aisdv1.storage.db.cache.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.shifthackz.aisdv1.storage.db.cache.contract.StableDiffusionLoraContract
import com.shifthackz.aisdv1.storage.db.cache.entity.StableDiffusionLoraEntity

@Dao
interface StableDiffusionLoraDao {

    @Query("SELECT * FROM ${StableDiffusionLoraContract.TABLE}")
    suspend fun queryAll(): List<StableDiffusionLoraEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(items: List<StableDiffusionLoraEntity>)

    @Query("DELETE FROM ${StableDiffusionLoraContract.TABLE}")
    suspend fun deleteAll()
}
