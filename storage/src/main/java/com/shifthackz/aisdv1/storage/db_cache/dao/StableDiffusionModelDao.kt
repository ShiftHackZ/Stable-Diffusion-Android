package com.shifthackz.aisdv1.storage.db_cache.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.shifthackz.aisdv1.storage.db_cache.contract.StableDiffusionModelContract
import com.shifthackz.aisdv1.storage.db_cache.entity.StableDiffusionModelEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

@Dao
interface StableDiffusionModelDao {

    @Query("SELECT * FROM ${StableDiffusionModelContract.TABLE}")
    fun queryAll(): Single<List<StableDiffusionModelEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertList(items: List<StableDiffusionModelEntity>): Completable
}
