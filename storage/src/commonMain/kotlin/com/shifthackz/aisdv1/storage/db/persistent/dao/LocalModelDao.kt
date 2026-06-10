package com.shifthackz.aisdv1.storage.db.persistent.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.shifthackz.aisdv1.storage.db.persistent.contract.LocalModelContract
import com.shifthackz.aisdv1.storage.db.persistent.entity.LocalModelEntity
import kotlinx.coroutines.flow.Flow

/**
 * Defines the `LocalModelDao` contract for the SDAI storage layer.
 *
 * @author Dmitriy Moroz
 */
@Dao
interface LocalModelDao {

    /**
     * Executes the `query` step in the SDAI storage layer.
     *
     * @return Result produced by `query`.
     * @author Dmitriy Moroz
     */
    @Query("SELECT * FROM ${LocalModelContract.TABLE}")
    suspend fun query(): List<LocalModelEntity>

    /**
     * Executes the `queryByType` step in the SDAI storage layer.
     *
     * @param type type value consumed by the API.
     * @return Result produced by `queryByType`.
     * @author Dmitriy Moroz
     */
    @Query("SELECT * FROM ${LocalModelContract.TABLE} WHERE ${LocalModelContract.TYPE} = :type")
    suspend fun queryByType(type: String): List<LocalModelEntity>

    /**
     * Loads SDAI data through `observe`.
     *
     * @return Result produced by `observe`.
     * @author Dmitriy Moroz
     */
    @Query("SELECT * FROM ${LocalModelContract.TABLE}")
    fun observe(): Flow<List<LocalModelEntity>>

    /**
     * Loads SDAI data through `observeByType`.
     *
     * @param type type value consumed by the API.
     * @return Result produced by `observeByType`.
     * @author Dmitriy Moroz
     */
    @Query("SELECT * FROM ${LocalModelContract.TABLE} WHERE ${LocalModelContract.TYPE} = :type")
    fun observeByType(type: String): Flow<List<LocalModelEntity>>

    /**
     * Executes the `queryById` step in the SDAI storage layer.
     *
     * @param id identifier of the target entity.
     * @return Result produced by `queryById`.
     * @author Dmitriy Moroz
     */
    @Query("SELECT * FROM ${LocalModelContract.TABLE} WHERE ${LocalModelContract.ID} = :id LIMIT 1")
    suspend fun queryById(id: String): LocalModelEntity

    /**
     * Performs the SDAI side effect handled by `insert`.
     *
     * @param item item value consumed by the API.
     * @author Dmitriy Moroz
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: LocalModelEntity)

    /**
     * Performs the SDAI side effect handled by `insertList`.
     *
     * @param items items value consumed by the API.
     * @author Dmitriy Moroz
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(items: List<LocalModelEntity>)

    /**
     * Performs the SDAI side effect handled by `deleteAll`.
     *
     * @author Dmitriy Moroz
     */
    @Query("DELETE FROM ${LocalModelContract.TABLE}")
    suspend fun deleteAll()
}
