package com.shifthackz.aisdv1.domain.datasource

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import kotlinx.coroutines.flow.Flow

/**
 * Defines the `GenerationResultDataSource` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
sealed interface GenerationResultDataSource {

    /**
     * Defines the `Local` contract for the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    interface Local : GenerationResultDataSource {
        /**
         * Performs the SDAI side effect handled by `insert`.
         *
         * @param result result value consumed by the API.
         * @return Result produced by `insert`.
         * @author Dmitriy Moroz
         */
        suspend fun insert(result: AiGenerationResult): Long
        /**
         * Executes the `queryAll` step in the SDAI domain layer.
         *
         * @return Result produced by `queryAll`.
         * @author Dmitriy Moroz
         */
        suspend fun queryAll(): List<AiGenerationResult>
        /**
         * Executes the `queryPage` step in the SDAI domain layer.
         *
         * @param limit limit value consumed by the API.
         * @param offset offset value consumed by the API.
         * @return Result produced by `queryPage`.
         * @author Dmitriy Moroz
         */
        suspend fun queryPage(limit: Int, offset: Int): List<AiGenerationResult>
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
         * Loads SDAI data through `observeCount`.
         *
         * @return Result produced by `observeCount`.
         * @author Dmitriy Moroz
         */
        fun observeCount(): Flow<Int>
        /**
         * Executes the `queryById` step in the SDAI domain layer.
         *
         * @param id identifier of the target entity.
         * @return Result produced by `queryById`.
         * @author Dmitriy Moroz
         */
        suspend fun queryById(id: Long): AiGenerationResult
        /**
         * Executes the `queryByIdList` step in the SDAI domain layer.
         *
         * @param idList id list value consumed by the API.
         * @return Result produced by `queryByIdList`.
         * @author Dmitriy Moroz
         */
        suspend fun queryByIdList(idList: List<Long>): List<AiGenerationResult>
        /**
         * Performs the SDAI side effect handled by `updateHiddenByIdList`.
         *
         * @param idList id list value consumed by the API.
         * @param hidden hidden value consumed by the API.
         * @author Dmitriy Moroz
         */
        suspend fun updateHiddenByIdList(idList: List<Long>, hidden: Boolean)
        /**
         * Performs the SDAI side effect handled by `updateLikedByIdList`.
         *
         * @param idList id list value consumed by the API.
         * @param liked liked value consumed by the API.
         * @author Dmitriy Moroz
         */
        suspend fun updateLikedByIdList(idList: List<Long>, liked: Boolean)
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
    }
}
