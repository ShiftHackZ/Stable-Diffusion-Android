package com.shifthackz.aisdv1.domain.datasource

import com.shifthackz.aisdv1.domain.entity.StableDiffusionModel
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials

/**
 * Defines the `StableDiffusionModelsDataSource` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
sealed interface StableDiffusionModelsDataSource {

    /**
     * Defines the `Remote` contract for the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    interface Remote : StableDiffusionModelsDataSource {
        /**
         * Loads SDAI data through `fetchSdModels`.
         *
         * @param baseUrl base url value consumed by the API.
         * @param credentials credentials value consumed by the API.
         * @return Result produced by `fetchSdModels`.
         * @author Dmitriy Moroz
         */
        suspend fun fetchSdModels(
            baseUrl: String,
            credentials: AuthorizationCredentials,
        ): List<StableDiffusionModel>
    }

    /**
     * Defines the `Local` contract for the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    interface Local : StableDiffusionModelsDataSource {
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
