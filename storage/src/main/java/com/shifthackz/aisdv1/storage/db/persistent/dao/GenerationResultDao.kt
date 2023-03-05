package com.shifthackz.aisdv1.storage.db.persistent.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.shifthackz.aisdv1.storage.db.persistent.contract.GenerationResultContract
import com.shifthackz.aisdv1.storage.db.persistent.entity.GenerationResultEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

@Dao
interface GenerationResultDao {

    @Query("SELECT * FROM ${GenerationResultContract.TABLE}")
    fun query(): Single<List<GenerationResultEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: GenerationResultEntity): Completable
}
