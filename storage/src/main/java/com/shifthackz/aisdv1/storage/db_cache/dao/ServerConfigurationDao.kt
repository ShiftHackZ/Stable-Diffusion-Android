package com.shifthackz.aisdv1.storage.db_cache.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.shifthackz.aisdv1.storage.db_cache.contract.ServerConfigurationContract
import com.shifthackz.aisdv1.storage.db_cache.entity.ServerConfigurationEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

@Dao
interface ServerConfigurationDao {

    @Query("SELECT * FROM ${ServerConfigurationContract.TABLE} LIMIT 1")
    fun query(): Single<ServerConfigurationEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: ServerConfigurationEntity): Completable
}
