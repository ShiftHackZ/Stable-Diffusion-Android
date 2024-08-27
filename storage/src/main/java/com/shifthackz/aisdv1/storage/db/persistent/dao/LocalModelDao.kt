package com.shifthackz.aisdv1.storage.db.persistent.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.shifthackz.aisdv1.storage.db.persistent.contract.LocalModelContract
import com.shifthackz.aisdv1.storage.db.persistent.entity.LocalModelEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single

@Dao
interface LocalModelDao {

    @Query("SELECT * FROM ${LocalModelContract.TABLE}")
    fun query(): Single<List<LocalModelEntity>>

    @Query("SELECT * FROM ${LocalModelContract.TABLE} WHERE ${LocalModelContract.TYPE} = :type")
    fun queryByType(type: String): Single<List<LocalModelEntity>>

    @Query("SELECT * FROM ${LocalModelContract.TABLE}")
    fun observe(): Flowable<List<LocalModelEntity>>

    @Query("SELECT * FROM ${LocalModelContract.TABLE} WHERE ${LocalModelContract.TYPE} = :type")
    fun observeByType(type: String): Flowable<List<LocalModelEntity>>

    @Query("SELECT * FROM ${LocalModelContract.TABLE} WHERE ${LocalModelContract.ID} = :id LIMIT 1")
    fun queryById(id: String): Single<LocalModelEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: LocalModelEntity): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertList(items: List<LocalModelEntity>): Completable
}
