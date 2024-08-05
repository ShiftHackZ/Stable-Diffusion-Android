package com.shifthackz.aisdv1.storage.db.cache.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.shifthackz.aisdv1.storage.db.cache.contract.SwarmUiModelContract
import com.shifthackz.aisdv1.storage.db.cache.entity.SwarmUiModelEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

@Dao
interface SwarmUiModelDao {

    @Query("SELECT * FROM ${SwarmUiModelContract.TABLE}")
    fun queryAll(): Single<List<SwarmUiModelEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertList(items: List<SwarmUiModelEntity>): Completable

    @Query("DELETE FROM ${SwarmUiModelContract.TABLE}")
    fun deleteAll(): Completable
}
