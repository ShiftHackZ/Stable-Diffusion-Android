package com.shifthackz.aisdv1.storage.db.cache.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.shifthackz.aisdv1.storage.db.cache.contract.StableDiffusionHyperNetworkContract
import com.shifthackz.aisdv1.storage.db.cache.entity.StableDiffusionHyperNetworkEntity

@Dao
interface StableDiffusionHyperNetworkDao {

    @Query("SELECT * FROM ${StableDiffusionHyperNetworkContract.TABLE}")
    suspend fun queryAll(): List<StableDiffusionHyperNetworkEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(items: List<StableDiffusionHyperNetworkEntity>)

    @Query("DELETE FROM ${StableDiffusionHyperNetworkContract.TABLE}")
    suspend fun deleteAll()
}
