package com.shifthackz.aisdv1.domain.repository

import com.shifthackz.aisdv1.domain.entity.StableDiffusionHyperNetwork

/**
 * Defines the `StableDiffusionHyperNetworksRepository` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
interface StableDiffusionHyperNetworksRepository {
    /**
     * Loads SDAI data through `fetchHyperNetworks`.
     *
     * @author Dmitriy Moroz
     */
    suspend fun fetchHyperNetworks()
    /**
     * Loads SDAI data through `fetchAndGetHyperNetworks`.
     *
     * @return Result produced by `fetchAndGetHyperNetworks`.
     * @author Dmitriy Moroz
     */
    suspend fun fetchAndGetHyperNetworks(): List<StableDiffusionHyperNetwork>
    /**
     * Loads SDAI data through `getHyperNetworks`.
     *
     * @return Result produced by `getHyperNetworks`.
     * @author Dmitriy Moroz
     */
    suspend fun getHyperNetworks(): List<StableDiffusionHyperNetwork>
}
