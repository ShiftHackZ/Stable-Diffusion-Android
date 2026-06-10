package com.shifthackz.aisdv1.storage.db.cache.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.shifthackz.aisdv1.storage.db.cache.contract.ServerConfigurationContract
import com.shifthackz.aisdv1.storage.db.cache.entity.ServerConfigurationEntity

@Dao
interface ServerConfigurationDao {

    @Query("SELECT * FROM ${ServerConfigurationContract.TABLE} LIMIT 1")
    suspend fun query(): ServerConfigurationEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ServerConfigurationEntity)

    @Query("DELETE FROM ${ServerConfigurationContract.TABLE}")
    suspend fun deleteAll()
}
