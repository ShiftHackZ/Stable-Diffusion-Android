package com.shifthackz.aisdv1.storage.db.persistent.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.shifthackz.aisdv1.storage.db.persistent.contract.HuggingFaceModelContract
import com.shifthackz.aisdv1.storage.db.persistent.entity.HuggingFaceModelEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

@Dao
interface HuggingFaceModelDao {

    @Query("SELECT * FROM ${HuggingFaceModelContract.TABLE}")
    fun query(): Single<List<HuggingFaceModelEntity>>

    @Query("SELECT * FROM ${HuggingFaceModelContract.TABLE} WHERE ${HuggingFaceModelContract.ID} = :id LIMIT 1")
    fun queryById(id: String): Single<HuggingFaceModelEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: HuggingFaceModelEntity): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertList(items: List<HuggingFaceModelEntity>): Completable

    @Query("DELETE FROM ${HuggingFaceModelContract.TABLE}")
    fun deleteAll(): Completable
}
