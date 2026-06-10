package com.shifthackz.aisdv1.storage.db.persistent.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.shifthackz.aisdv1.storage.db.persistent.contract.HuggingFaceModelContract
import com.shifthackz.aisdv1.storage.db.persistent.entity.HuggingFaceModelEntity

@Dao
interface HuggingFaceModelDao {

    @Query("SELECT * FROM ${HuggingFaceModelContract.TABLE}")
    suspend fun query(): List<HuggingFaceModelEntity>

    @Query("SELECT * FROM ${HuggingFaceModelContract.TABLE} WHERE ${HuggingFaceModelContract.ID} = :id LIMIT 1")
    suspend fun queryById(id: String): HuggingFaceModelEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: HuggingFaceModelEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(items: List<HuggingFaceModelEntity>)

    @Query("DELETE FROM ${HuggingFaceModelContract.TABLE}")
    suspend fun deleteAll()
}
