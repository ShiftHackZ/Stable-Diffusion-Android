package com.shifthackz.aisdv1.domain.datasource

import com.shifthackz.aisdv1.domain.entity.StableDiffusionHyperNetwork
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials

/**
 * Defines the `StableDiffusionHyperNetworksDataSource` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
sealed interface StableDiffusionHyperNetworksDataSource {

    /**
     * Defines the `Remote` contract for the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    interface Remote : StableDiffusionHyperNetworksDataSource {
        /**
         * Loads SDAI data through `fetchHyperNetworks`.
         *
         * @param baseUrl base url value consumed by the API.
         * @param credentials credentials value consumed by the API.
         * @return Result produced by `fetchHyperNetworks`.
         * @author Dmitriy Moroz
         */
        suspend fun fetchHyperNetworks(
            baseUrl: String,
            credentials: AuthorizationCredentials,
        ): List<StableDiffusionHyperNetwork>
    }

    /**
     * Defines the `Local` contract for the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    interface Local : StableDiffusionHyperNetworksDataSource {
        /**
         * Loads SDAI data through `getHyperNetworks`.
         *
         * @return Result produced by `getHyperNetworks`.
         * @author Dmitriy Moroz
         */
        suspend fun getHyperNetworks(): List<StableDiffusionHyperNetwork>
        /**
         * Performs the SDAI side effect handled by `insertHyperNetworks`.
         *
         * @param list list value consumed by the API.
         * @author Dmitriy Moroz
         */
        suspend fun insertHyperNetworks(list: List<StableDiffusionHyperNetwork>)
    }
}
