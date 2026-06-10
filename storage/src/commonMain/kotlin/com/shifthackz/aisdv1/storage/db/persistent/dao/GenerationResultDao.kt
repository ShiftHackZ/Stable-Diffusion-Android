package com.shifthackz.aisdv1.storage.db.persistent.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.shifthackz.aisdv1.storage.db.persistent.contract.GenerationResultContract
import com.shifthackz.aisdv1.storage.db.persistent.entity.GenerationResultEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GenerationResultDao {

    @Query("SELECT * FROM ${GenerationResultContract.TABLE} ORDER BY ${GenerationResultContract.CREATED_AT} DESC")
    suspend fun query(): List<GenerationResultEntity>

    @Query("SELECT * FROM ${GenerationResultContract.TABLE} ORDER BY ${GenerationResultContract.CREATED_AT} DESC LIMIT :limit OFFSET :offset ")
    suspend fun queryPage(limit: Int, offset: Int): List<GenerationResultEntity>

    @Query("SELECT * FROM ${GenerationResultContract.TABLE} ORDER BY ${GenerationResultContract.CREATED_AT} DESC LIMIT :limit OFFSET :offset ")
    fun observePage(limit: Int, offset: Int): Flow<List<GenerationResultEntity>>

    @Query("SELECT COUNT(*) FROM ${GenerationResultContract.TABLE}")
    fun observeCount(): Flow<Int>

    @Query("SELECT * FROM ${GenerationResultContract.TABLE} WHERE ${GenerationResultContract.ID} = :id LIMIT 1")
    suspend fun queryById(id: Long): GenerationResultEntity

    @Query("SELECT * FROM ${GenerationResultContract.TABLE} WHERE ${GenerationResultContract.ID} IN (:idList)")
    suspend fun queryByIdList(idList: List<Long>): List<GenerationResultEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: GenerationResultEntity): Long

    @Query("DELETE FROM ${GenerationResultContract.TABLE} WHERE ${GenerationResultContract.ID} = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM ${GenerationResultContract.TABLE} WHERE ${GenerationResultContract.ID} IN (:idList)")
    suspend fun deleteByIdList(idList: List<Long>)

    @Query("DELETE FROM ${GenerationResultContract.TABLE}")
    suspend fun deleteAll()
}
