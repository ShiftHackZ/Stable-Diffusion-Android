package com.shifthackz.aisdv1.storage.db.cache.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.shifthackz.aisdv1.storage.db.cache.contract.StableDiffusionEmbeddingContract
import com.shifthackz.aisdv1.storage.db.cache.entity.StableDiffusionEmbeddingEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

@Dao
interface StableDiffusionEmbeddingDao {

    @Query("SELECT * FROM ${StableDiffusionEmbeddingContract.TABLE}")
    fun queryAll(): Single<List<StableDiffusionEmbeddingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertList(items: List<StableDiffusionEmbeddingEntity>): Completable

    @Query("DELETE FROM ${StableDiffusionEmbeddingContract.TABLE}")
    fun deleteAll(): Completable
}
