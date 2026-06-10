package com.shifthackz.aisdv1.storage.db.cache.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.shifthackz.aisdv1.storage.db.cache.contract.ServerConfigurationContract
import com.shifthackz.aisdv1.storage.db.cache.entity.ServerConfigurationEntity

/**
 * Defines the `ServerConfigurationDao` contract for the SDAI storage layer.
 *
 * @author Dmitriy Moroz
 */
@Dao
interface ServerConfigurationDao {

    /**
     * Executes the `query` step in the SDAI storage layer.
     *
     * @return Result produced by `query`.
     * @author Dmitriy Moroz
     */
    @Query("SELECT * FROM ${ServerConfigurationContract.TABLE} LIMIT 1")
    suspend fun query(): ServerConfigurationEntity

    /**
     * Performs the SDAI side effect handled by `insert`.
     *
     * @param item item value consumed by the API.
     * @author Dmitriy Moroz
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ServerConfigurationEntity)

    /**
     * Performs the SDAI side effect handled by `deleteAll`.
     *
     * @author Dmitriy Moroz
     */
    @Query("DELETE FROM ${ServerConfigurationContract.TABLE}")
    suspend fun deleteAll()
}
