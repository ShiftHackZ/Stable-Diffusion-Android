package com.shifthackz.aisdv1.domain.usecase.sdhypernet

import com.shifthackz.aisdv1.domain.repository.StableDiffusionHyperNetworksRepository

internal class FetchAndGetHyperNetworksUseCaseImpl(
    private val stableDiffusionHyperNetworksRepository: StableDiffusionHyperNetworksRepository,
) : FetchAndGetHyperNetworksUseCase {
    override suspend fun invoke() = stableDiffusionHyperNetworksRepository.fetchAndGetHyperNetworks()
}
