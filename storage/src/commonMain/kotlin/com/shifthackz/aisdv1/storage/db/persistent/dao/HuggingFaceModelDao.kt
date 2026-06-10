package com.shifthackz.aisdv1.storage.db.persistent.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.shifthackz.aisdv1.storage.db.persistent.contract.HuggingFaceModelContract
import com.shifthackz.aisdv1.storage.db.persistent.entity.HuggingFaceModelEntity

/**
 * Defines the `HuggingFaceModelDao` contract for the SDAI storage layer.
 *
 * @author Dmitriy Moroz
 */
@Dao
interface HuggingFaceModelDao {

    /**
     * Executes the `query` step in the SDAI storage layer.
     *
     * @return Result produced by `query`.
     * @author Dmitriy Moroz
     */
    @Query("SELECT * FROM ${HuggingFaceModelContract.TABLE}")
    suspend fun query(): List<HuggingFaceModelEntity>

    /**
     * Executes the `queryById` step in the SDAI storage layer.
     *
     * @param id identifier of the target entity.
     * @return Result produced by `queryById`.
     * @author Dmitriy Moroz
     */
    @Query("SELECT * FROM ${HuggingFaceModelContract.TABLE} WHERE ${HuggingFaceModelContract.ID} = :id LIMIT 1")
    suspend fun queryById(id: String): HuggingFaceModelEntity

    /**
     * Performs the SDAI side effect handled by `insert`.
     *
     * @param item item value consumed by the API.
     * @author Dmitriy Moroz
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: HuggingFaceModelEntity)

    /**
     * Performs the SDAI side effect handled by `insertList`.
     *
     * @param items items value consumed by the API.
     * @author Dmitriy Moroz
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(items: List<HuggingFaceModelEntity>)

    /**
     * Performs the SDAI side effect handled by `deleteAll`.
     *
     * @author Dmitriy Moroz
     */
    @Query("DELETE FROM ${HuggingFaceModelContract.TABLE}")
    suspend fun deleteAll()
}
