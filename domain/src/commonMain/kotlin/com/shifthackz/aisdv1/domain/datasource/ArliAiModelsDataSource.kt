package com.shifthackz.aisdv1.domain.datasource

import com.shifthackz.aisdv1.domain.entity.StableDiffusionModel

/**
 * Defines the `ArliAiModelsDataSource` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
sealed interface ArliAiModelsDataSource {

    /**
     * Defines the `Remote` contract for the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    interface Remote : ArliAiModelsDataSource {
        /**
         * Loads SDAI data through `fetchModels`.
         *
         * @param apiKey api key value consumed by the API.
         * @return Result produced by `fetchModels`.
         * @author Dmitriy Moroz
         */
        suspend fun fetchModels(apiKey: String): List<StableDiffusionModel>
    }

    /**
     * Defines the `Local` contract for the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    interface Local : ArliAiModelsDataSource {
        /**
         * Loads SDAI data through `getModels`.
         *
         * @return Result produced by `getModels`.
         * @author Dmitriy Moroz
         */
        suspend fun getModels(): List<StableDiffusionModel>

        /**
         * Performs the SDAI side effect handled by `insertModels`.
         *
         * @param models models value consumed by the API.
         * @author Dmitriy Moroz
         */
        suspend fun insertModels(models: List<StableDiffusionModel>)
    }
}
