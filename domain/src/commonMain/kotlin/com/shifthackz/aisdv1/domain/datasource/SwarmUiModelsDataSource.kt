package com.shifthackz.aisdv1.domain.datasource

import com.shifthackz.aisdv1.domain.entity.SwarmUiModel

/**
 * Defines the `SwarmUiModelsDataSource` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
interface SwarmUiModelsDataSource {

    /**
     * Defines the `Local` contract for the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    interface Local : SwarmUiModelsDataSource {
        /**
         * Loads SDAI data through `getModels`.
         *
         * @return Result produced by `getModels`.
         * @author Dmitriy Moroz
         */
        suspend fun getModels(): List<SwarmUiModel>
        /**
         * Performs the SDAI side effect handled by `insertModels`.
         *
         * @param models models value consumed by the API.
         * @author Dmitriy Moroz
         */
        suspend fun insertModels(models: List<SwarmUiModel>)
    }
}
