package com.shifthackz.aisdv1.storage.db.persistent.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.shifthackz.aisdv1.storage.db.persistent.contract.LocalModelContract
import com.shifthackz.aisdv1.storage.db.persistent.entity.LocalModelEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LocalModelDao {

    @Query("SELECT * FROM ${LocalModelContract.TABLE}")
    suspend fun query(): List<LocalModelEntity>

    @Query("SELECT * FROM ${LocalModelContract.TABLE} WHERE ${LocalModelContract.TYPE} = :type")
    suspend fun queryByType(type: String): List<LocalModelEntity>

    @Query("SELECT * FROM ${LocalModelContract.TABLE}")
    fun observe(): Flow<List<LocalModelEntity>>

    @Query("SELECT * FROM ${LocalModelContract.TABLE} WHERE ${LocalModelContract.TYPE} = :type")
    fun observeByType(type: String): Flow<List<LocalModelEntity>>

    @Query("SELECT * FROM ${LocalModelContract.TABLE} WHERE ${LocalModelContract.ID} = :id LIMIT 1")
    suspend fun queryById(id: String): LocalModelEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: LocalModelEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(items: List<LocalModelEntity>)

    @Query("DELETE FROM ${LocalModelContract.TABLE}")
    suspend fun deleteAll()
}
