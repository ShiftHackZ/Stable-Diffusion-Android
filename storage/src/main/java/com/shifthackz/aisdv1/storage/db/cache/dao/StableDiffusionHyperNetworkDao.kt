package com.shifthackz.aisdv1.storage.db.cache.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.shifthackz.aisdv1.storage.db.cache.contract.StableDiffusionHyperNetworkContract
import com.shifthackz.aisdv1.storage.db.cache.entity.StableDiffusionHyperNetworkEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

@Dao
interface StableDiffusionHyperNetworkDao {

    @Query("SELECT * FROM ${StableDiffusionHyperNetworkContract.TABLE}")
    fun queryAll(): Single<List<StableDiffusionHyperNetworkEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertList(items: List<StableDiffusionHyperNetworkEntity>): Completable
}
