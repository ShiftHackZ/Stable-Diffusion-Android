package com.shifthackz.aisdv1.storage.db.cache.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.shifthackz.aisdv1.storage.db.cache.contract.StableDiffusionModelContract
import com.shifthackz.aisdv1.storage.db.cache.entity.StableDiffusionModelEntity

@Dao
interface StableDiffusionModelDao {

    @Query("SELECT * FROM ${StableDiffusionModelContract.TABLE}")
    suspend fun queryAll(): List<StableDiffusionModelEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(items: List<StableDiffusionModelEntity>)

    @Query("DELETE FROM ${StableDiffusionModelContract.TABLE}")
    suspend fun deleteAll()
}
