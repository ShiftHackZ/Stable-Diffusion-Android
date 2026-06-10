package com.shifthackz.aisdv1.storage.db.persistent.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.shifthackz.aisdv1.storage.db.persistent.contract.SupporterContract
import com.shifthackz.aisdv1.storage.db.persistent.entity.SupporterEntity

/**
 * Defines the `SupporterDao` contract for the SDAI storage layer.
 *
 * @author Dmitriy Moroz
 */
@Dao
interface SupporterDao {

    /**
     * Executes the `queryAll` step in the SDAI storage layer.
     *
     * @return Result produced by `queryAll`.
     * @author Dmitriy Moroz
     */
    @Query("SELECT * FROM ${SupporterContract.TABLE} ORDER BY ${SupporterContract.DATE} DESC")
    suspend fun queryAll(): List<SupporterEntity>

    /**
     * Performs the SDAI side effect handled by `insertList`.
     *
     * @param items items value consumed by the API.
     * @author Dmitriy Moroz
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(items: List<SupporterEntity>)

    /**
     * Performs the SDAI side effect handled by `deleteAll`.
     *
     * @author Dmitriy Moroz
     */
    @Query("DELETE FROM ${SupporterContract.TABLE}")
    suspend fun deleteAll()
}
