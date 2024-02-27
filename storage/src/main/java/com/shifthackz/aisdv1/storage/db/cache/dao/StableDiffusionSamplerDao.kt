package com.shifthackz.aisdv1.storage.db.cache.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.shifthackz.aisdv1.storage.db.cache.contract.StableDiffusionSamplerContract
import com.shifthackz.aisdv1.storage.db.cache.entity.StableDiffusionSamplerEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

@Dao
interface StableDiffusionSamplerDao {

    @Query("SELECT * FROM ${StableDiffusionSamplerContract.TABLE}")
    fun queryAll(): Single<List<StableDiffusionSamplerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertList(items: List<StableDiffusionSamplerEntity>): Completable

    @Query("DELETE FROM ${StableDiffusionSamplerContract.TABLE}")
    fun deleteAll(): Completable
}
