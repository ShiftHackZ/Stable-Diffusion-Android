package com.shifthackz.aisdv1.domain.usecase.sdhypernet

import com.shifthackz.aisdv1.domain.repository.StableDiffusionHyperNetworksRepository

/**
 * Implements `FetchAndGetHyperNetworksUseCase` behavior in the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
internal class FetchAndGetHyperNetworksUseCaseImpl(
    /**
     * Exposes the `stableDiffusionHyperNetworksRepository` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val stableDiffusionHyperNetworksRepository: StableDiffusionHyperNetworksRepository,
) : FetchAndGetHyperNetworksUseCase {
    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    override suspend fun invoke() = stableDiffusionHyperNetworksRepository.fetchAndGetHyperNetworks()
}
