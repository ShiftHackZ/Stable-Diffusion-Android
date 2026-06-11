package com.shifthackz.aisdv1.storage.db.persistent.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.shifthackz.aisdv1.storage.db.persistent.contract.GenerationResultContract
import com.shifthackz.aisdv1.storage.db.persistent.entity.GenerationResultEntity
import kotlinx.coroutines.flow.Flow

/**
 * Defines the `GenerationResultDao` contract for the SDAI storage layer.
 *
 * @author Dmitriy Moroz
 */
@Dao
interface GenerationResultDao {

    /**
     * Executes the `query` step in the SDAI storage layer.
     *
     * @return Result produced by `query`.
     * @author Dmitriy Moroz
     */
    @Query("SELECT * FROM ${GenerationResultContract.TABLE} ORDER BY ${GenerationResultContract.CREATED_AT} DESC")
    suspend fun query(): List<GenerationResultEntity>

    /**
     * Executes the `queryPage` step in the SDAI storage layer.
     *
     * @param limit limit value consumed by the API.
     * @param offset offset value consumed by the API.
     * @return Result produced by `queryPage`.
     * @author Dmitriy Moroz
     */
    @Query("SELECT * FROM ${GenerationResultContract.TABLE} ORDER BY ${GenerationResultContract.CREATED_AT} DESC LIMIT :limit OFFSET :offset ")
    suspend fun queryPage(limit: Int, offset: Int): List<GenerationResultEntity>

    /**
     * Loads SDAI data through `observePage`.
     *
     * @param limit limit value consumed by the API.
     * @param offset offset value consumed by the API.
     * @return Result produced by `observePage`.
     * @author Dmitriy Moroz
     */
    @Query("SELECT * FROM ${GenerationResultContract.TABLE} ORDER BY ${GenerationResultContract.CREATED_AT} DESC LIMIT :limit OFFSET :offset ")
    fun observePage(limit: Int, offset: Int): Flow<List<GenerationResultEntity>>

    /**
     * Loads SDAI data through `observeCount`.
     *
     * @return Result produced by `observeCount`.
     * @author Dmitriy Moroz
     */
    @Query("SELECT COUNT(*) FROM ${GenerationResultContract.TABLE}")
    fun observeCount(): Flow<Int>

    /**
     * Executes the `queryById` step in the SDAI storage layer.
     *
     * @param id identifier of the target entity.
     * @return Result produced by `queryById`.
     * @author Dmitriy Moroz
     */
    @Query("SELECT * FROM ${GenerationResultContract.TABLE} WHERE ${GenerationResultContract.ID} = :id LIMIT 1")
    suspend fun queryById(id: Long): GenerationResultEntity

    /**
     * Executes the `queryByIdList` step in the SDAI storage layer.
     *
     * @param idList id list value consumed by the API.
     * @return Result produced by `queryByIdList`.
     * @author Dmitriy Moroz
     */
    @Query("SELECT * FROM ${GenerationResultContract.TABLE} WHERE ${GenerationResultContract.ID} IN (:idList)")
    suspend fun queryByIdList(idList: List<Long>): List<GenerationResultEntity>

    /**
     * Performs the SDAI side effect handled by `updateHiddenByIdList`.
     *
     * @param idList id list value consumed by the API.
     * @param hidden hidden value consumed by the API.
     * @author Dmitriy Moroz
     */
    @Query("UPDATE ${GenerationResultContract.TABLE} SET ${GenerationResultContract.HIDDEN} = :hidden WHERE ${GenerationResultContract.ID} IN (:idList)")
    suspend fun updateHiddenByIdList(idList: List<Long>, hidden: Boolean)

    /**
     * Performs the SDAI side effect handled by `updateLikedByIdList`.
     *
     * @param idList id list value consumed by the API.
     * @param liked liked value consumed by the API.
     * @author Dmitriy Moroz
     */
    @Query("UPDATE ${GenerationResultContract.TABLE} SET ${GenerationResultContract.LIKED} = :liked WHERE ${GenerationResultContract.ID} IN (:idList)")
    suspend fun updateLikedByIdList(idList: List<Long>, liked: Boolean)

    /**
     * Performs the SDAI side effect handled by `insert`.
     *
     * @param item item value consumed by the API.
     * @return Result produced by `insert`.
     * @author Dmitriy Moroz
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: GenerationResultEntity): Long

    /**
     * Performs the SDAI side effect handled by `deleteById`.
     *
     * @param id identifier of the target entity.
     * @author Dmitriy Moroz
     */
    @Query("DELETE FROM ${GenerationResultContract.TABLE} WHERE ${GenerationResultContract.ID} = :id")
    suspend fun deleteById(id: Long)

    /**
     * Performs the SDAI side effect handled by `deleteByIdList`.
     *
     * @param idList id list value consumed by the API.
     * @author Dmitriy Moroz
     */
    @Query("DELETE FROM ${GenerationResultContract.TABLE} WHERE ${GenerationResultContract.ID} IN (:idList)")
    suspend fun deleteByIdList(idList: List<Long>)

    /**
     * Performs the SDAI side effect handled by `deleteAll`.
     *
     * @author Dmitriy Moroz
     */
    @Query("DELETE FROM ${GenerationResultContract.TABLE}")
    suspend fun deleteAll()
}
