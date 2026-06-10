package com.shifthackz.aisdv1.storage.db.persistent.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.shifthackz.aisdv1.storage.db.persistent.contract.SupporterContract
import com.shifthackz.aisdv1.storage.db.persistent.entity.SupporterEntity

@Dao
interface SupporterDao {

    @Query("SELECT * FROM ${SupporterContract.TABLE} ORDER BY ${SupporterContract.DATE} DESC")
    suspend fun queryAll(): List<SupporterEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(items: List<SupporterEntity>)

    @Query("DELETE FROM ${SupporterContract.TABLE}")
    suspend fun deleteAll()
}
