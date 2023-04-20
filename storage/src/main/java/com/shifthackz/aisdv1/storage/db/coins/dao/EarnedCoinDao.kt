package com.shifthackz.aisdv1.storage.db.coins.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.shifthackz.aisdv1.storage.db.coins.contract.EarnedCoinContract
import com.shifthackz.aisdv1.storage.db.coins.entity.EarnedCoinEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

@Dao
interface EarnedCoinDao {

    @Query("SELECT COUNT(${EarnedCoinContract.ID}) FROM ${EarnedCoinContract.TABLE}")
    fun queryCount(): Single<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(items: List<EarnedCoinEntity>): Completable

    @Query("DELETE FROM ${EarnedCoinContract.TABLE} WHERE ${EarnedCoinContract.ID} = (SELECT MAX(${EarnedCoinContract.ID}) FROM ${EarnedCoinContract.TABLE})")
    fun deleteLast(): Completable
}
