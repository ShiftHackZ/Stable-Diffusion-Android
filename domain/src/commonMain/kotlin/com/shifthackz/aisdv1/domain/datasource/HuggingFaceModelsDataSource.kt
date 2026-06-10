package com.shifthackz.aisdv1.domain.datasource

import com.shifthackz.aisdv1.domain.entity.HuggingFaceModel

/**
 * Defines the `HuggingFaceModelsDataSource` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
sealed interface HuggingFaceModelsDataSource {

    /**
     * Defines the `Local` contract for the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    interface Local : HuggingFaceModelsDataSource {
        /**
         * Loads SDAI data through `getAll`.
         *
         * @return Result produced by `getAll`.
         * @author Dmitriy Moroz
         */
        suspend fun getAll(): List<HuggingFaceModel>
        /**
         * Performs the SDAI side effect handled by `save`.
         *
         * @param models models value consumed by the API.
         * @author Dmitriy Moroz
         */
        suspend fun save(models: List<HuggingFaceModel>)
    }
}
