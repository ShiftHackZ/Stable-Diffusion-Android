package com.shifthackz.aisdv1.storage.db.persistent.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.shifthackz.aisdv1.storage.db.persistent.contract.GenerationResultContract
import com.shifthackz.aisdv1.storage.db.persistent.entity.GenerationResultEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

@Dao
interface GenerationResultDao {

    @Query("SELECT * FROM ${GenerationResultContract.TABLE} ORDER BY ${GenerationResultContract.CREATED_AT} DESC")
    fun query(): Single<List<GenerationResultEntity>>

    @Query("SELECT * FROM ${GenerationResultContract.TABLE} ORDER BY ${GenerationResultContract.CREATED_AT} DESC LIMIT :limit OFFSET :offset ")
    fun queryPage(limit: Int, offset: Int): Single<List<GenerationResultEntity>>

    @Query("SELECT * FROM ${GenerationResultContract.TABLE} WHERE ${GenerationResultContract.ID} = :id LIMIT 1")
    fun queryById(id: Long): Single<GenerationResultEntity>

    @Query("SELECT * FROM ${GenerationResultContract.TABLE} WHERE ${GenerationResultContract.ID} IN (:idList)")
    fun queryByIdList(idList: List<Long>): Single<List<GenerationResultEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: GenerationResultEntity): Single<Long>

    @Query("DELETE FROM ${GenerationResultContract.TABLE} WHERE ${GenerationResultContract.ID} = :id")
    fun deleteById(id: Long): Completable

    @Query("DELETE FROM ${GenerationResultContract.TABLE} WHERE ${GenerationResultContract.ID} IN (:idList)")
    fun deleteByIdList(idList: List<Long>): Completable

    @Query("DELETE FROM ${GenerationResultContract.TABLE}")
    fun deleteAll(): Completable
}
