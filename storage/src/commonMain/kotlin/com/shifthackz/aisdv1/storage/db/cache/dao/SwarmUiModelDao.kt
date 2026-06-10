package com.shifthackz.aisdv1.storage.db.cache.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.shifthackz.aisdv1.storage.db.cache.contract.SwarmUiModelContract
import com.shifthackz.aisdv1.storage.db.cache.entity.SwarmUiModelEntity

@Dao
interface SwarmUiModelDao {

    @Query("SELECT * FROM ${SwarmUiModelContract.TABLE}")
    suspend fun queryAll(): List<SwarmUiModelEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(items: List<SwarmUiModelEntity>)

    @Query("DELETE FROM ${SwarmUiModelContract.TABLE}")
    suspend fun deleteAll()
}
