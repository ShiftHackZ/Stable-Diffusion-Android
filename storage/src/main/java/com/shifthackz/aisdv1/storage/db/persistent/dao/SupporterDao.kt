package com.shifthackz.aisdv1.storage.db.persistent.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.shifthackz.aisdv1.storage.db.persistent.contract.SupporterContract
import com.shifthackz.aisdv1.storage.db.persistent.entity.SupporterEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

@Dao
interface SupporterDao {

    @Query("SELECT * FROM ${SupporterContract.TABLE} ORDER BY ${SupporterContract.DATE} DESC")
    fun queryAll(): Single<List<SupporterEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertList(items: List<SupporterEntity>): Completable

    @Query("DELETE FROM ${SupporterContract.TABLE}")
    fun deleteAll(): Completable
}
