package com.shifthackz.aisdv1.domain.repository

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.AiGenerationResultPreview
import kotlinx.coroutines.flow.Flow

/**
 * Defines the `GenerationResultRepository` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
interface GenerationResultRepository {

    /**
     * Loads SDAI data through `getAll`.
     *
     * @return Result produced by `getAll`.
     * @author Dmitriy Moroz
     */
    suspend fun getAll(): List<AiGenerationResult>

    /**
     * Loads SDAI data through `getAllIds`.
     *
     * @return Result produced by `getAllIds`.
     * @author Dmitriy Moroz
     */
    suspend fun getAllIds(): List<Long>

    /**
     * Loads SDAI data through `getPage`.
     *
     * @param limit limit value consumed by the API.
     * @param offset offset value consumed by the API.
     * @return Result produced by `getPage`.
     * @author Dmitriy Moroz
     */
    suspend fun getPage(limit: Int, offset: Int): List<AiGenerationResult>

    /**
     * Loads SDAI data through `getPagePreview`.
     *
     * @param limit limit value consumed by the API.
     * @param offset offset value consumed by the API.
     * @return Result produced by `getPagePreview`.
     * @author Dmitriy Moroz
     */
    suspend fun getPagePreview(limit: Int, offset: Int): List<AiGenerationResultPreview>

    /**
     * Loads SDAI data through `observePage`.
     *
     * @param limit limit value consumed by the API.
     * @param offset offset value consumed by the API.
     * @return Result produced by `observePage`.
     * @author Dmitriy Moroz
     */
    fun observePage(limit: Int, offset: Int): Flow<List<AiGenerationResult>>

    /**
     * Loads SDAI data through `observePagePreview`.
     *
     * @param limit limit value consumed by the API.
     * @param offset offset value consumed by the API.
     * @return Result produced by `observePagePreview`.
     * @author Dmitriy Moroz
     */
    fun observePagePreview(limit: Int, offset: Int): Flow<List<AiGenerationResultPreview>>

    /**
     * Loads SDAI data through `observeCount`.
     *
     * @return Result produced by `observeCount`.
     * @author Dmitriy Moroz
     */
    fun observeCount(): Flow<Int>

    /**
     * Loads SDAI data through `getById`.
     *
     * @param id identifier of the target entity.
     * @return Result produced by `getById`.
     * @author Dmitriy Moroz
     */
    suspend fun getById(id: Long): AiGenerationResult

    /**
     * Loads SDAI data through `getByIds`.
     *
     * @param idList id list value consumed by the API.
     * @return Result produced by `getByIds`.
     * @author Dmitriy Moroz
     */
    suspend fun getByIds(idList: List<Long>): List<AiGenerationResult>

    /**
     * Performs the SDAI side effect handled by `insert`.
     *
     * @param result result value consumed by the API.
     * @return Result produced by `insert`.
     * @author Dmitriy Moroz
     */
    suspend fun insert(result: AiGenerationResult): Long

    /**
     * Performs the SDAI side effect handled by `deleteById`.
     *
     * @param id identifier of the target entity.
     * @author Dmitriy Moroz
     */
    suspend fun deleteById(id: Long)

    /**
     * Performs the SDAI side effect handled by `deleteByIdList`.
     *
     * @param idList id list value consumed by the API.
     * @author Dmitriy Moroz
     */
    suspend fun deleteByIdList(idList: List<Long>)

    /**
     * Performs the SDAI side effect handled by `deleteAll`.
     *
     * @author Dmitriy Moroz
     */
    suspend fun deleteAll()

    /**
     * Performs the SDAI side effect handled by `setVisibilityByIds`.
     *
     * @param ids ids value consumed by the API.
     * @param hidden hidden value consumed by the API.
     * @author Dmitriy Moroz
     */
    suspend fun setVisibilityByIds(ids: List<Long>, hidden: Boolean)

    /**
     * Performs the SDAI side effect handled by `setLikedByIds`.
     *
     * @param ids ids value consumed by the API.
     * @param liked liked value consumed by the API.
     * @author Dmitriy Moroz
     */
    suspend fun setLikedByIds(ids: List<Long>, liked: Boolean)

    /**
     * Converts SDAI data with `toggleVisibility`.
     *
     * @param id identifier of the target entity.
     * @return Result produced by `toggleVisibility`.
     * @author Dmitriy Moroz
     */
    suspend fun toggleVisibility(id: Long): Boolean

    /**
     * Converts SDAI data with `toggleLike`.
     *
     * @param id identifier of the target entity.
     * @return Result produced by `toggleLike`.
     * @author Dmitriy Moroz
     */
    suspend fun toggleLike(id: Long): Boolean
}
