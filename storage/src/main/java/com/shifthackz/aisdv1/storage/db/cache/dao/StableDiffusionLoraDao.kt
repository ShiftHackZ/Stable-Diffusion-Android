package com.shifthackz.aisdv1.storage.db.cache.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.shifthackz.aisdv1.storage.db.cache.contract.StableDiffusionLoraContract
import com.shifthackz.aisdv1.storage.db.cache.entity.StableDiffusionLoraEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

@Dao
interface StableDiffusionLoraDao {

    @Query("SELECT * FROM ${StableDiffusionLoraContract.TABLE}")
    fun queryAll(): Single<List<StableDiffusionLoraEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertList(items: List<StableDiffusionLoraEntity>): Completable

    @Query("DELETE FROM ${StableDiffusionLoraContract.TABLE}")
    fun deleteAll(): Completable
}
