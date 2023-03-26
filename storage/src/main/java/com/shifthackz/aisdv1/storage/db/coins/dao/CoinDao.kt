package com.shifthackz.aisdv1.storage.db.coins.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.shifthackz.aisdv1.storage.db.coins.contract.CoinContract
import com.shifthackz.aisdv1.storage.db.coins.entity.CoinEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable

@Dao
interface CoinDao {

    @Query("SELECT * FROM ${CoinContract.TABLE} WHERE ${CoinContract.DATE} BETWEEN :start AND :end")
    fun observeQueryAvailableCoinsForPeriod(start: Long, end: Long): Flowable<List<CoinEntity>>

    @Insert
    fun insert(item: CoinEntity): Completable
}
