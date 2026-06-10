package com.shifthackz.aisdv1.domain.datasource

import com.shifthackz.aisdv1.domain.entity.StableDiffusionSampler
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials

/**
 * Defines the `StableDiffusionSamplersDataSource` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
sealed interface StableDiffusionSamplersDataSource {

    /**
     * Defines the `Remote` contract for the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    interface Remote : StableDiffusionSamplersDataSource {
        /**
         * Loads SDAI data through `fetchSamplers`.
         *
         * @param baseUrl base url value consumed by the API.
         * @param credentials credentials value consumed by the API.
         * @return Result produced by `fetchSamplers`.
         * @author Dmitriy Moroz
         */
        suspend fun fetchSamplers(
            baseUrl: String,
            credentials: AuthorizationCredentials,
        ): List<StableDiffusionSampler>
    }

    /**
     * Defines the `Local` contract for the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    interface Local : StableDiffusionSamplersDataSource {
        /**
         * Loads SDAI data through `getSamplers`.
         *
         * @return Result produced by `getSamplers`.
         * @author Dmitriy Moroz
         */
        suspend fun getSamplers(): List<StableDiffusionSampler>
        /**
         * Performs the SDAI side effect handled by `insertSamplers`.
         *
         * @param samplers samplers value consumed by the API.
         * @author Dmitriy Moroz
         */
        suspend fun insertSamplers(samplers: List<StableDiffusionSampler>)
    }
}
