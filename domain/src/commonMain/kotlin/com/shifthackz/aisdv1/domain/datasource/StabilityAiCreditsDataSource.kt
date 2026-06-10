package com.shifthackz.aisdv1.domain.datasource

import kotlinx.coroutines.flow.Flow

/**
 * Defines the `StabilityAiCreditsDataSource` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
sealed interface StabilityAiCreditsDataSource {

    /**
     * Defines the `Local` contract for the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    interface Local : StabilityAiCreditsDataSource {
        /**
         * Loads SDAI data through `get`.
         *
         * @return Result produced by `get`.
         * @author Dmitriy Moroz
         */
        suspend fun get(): Float
        /**
         * Performs the SDAI side effect handled by `save`.
         *
         * @param value value value consumed by the API.
         * @author Dmitriy Moroz
         */
        suspend fun save(value: Float)
        /**
         * Loads SDAI data through `observe`.
         *
         * @return Result produced by `observe`.
         * @author Dmitriy Moroz
         */
        fun observe(): Flow<Float>
    }
}
