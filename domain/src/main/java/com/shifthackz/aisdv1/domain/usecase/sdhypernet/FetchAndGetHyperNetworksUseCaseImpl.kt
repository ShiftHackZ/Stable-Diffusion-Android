package com.shifthackz.aisdv1.domain.usecase.sdhypernet

import com.shifthackz.aisdv1.domain.repository.StableDiffusionHyperNetworksRepository

internal class FetchAndGetHyperNetworksUseCaseImpl(
    private val stableDiffusionHyperNetworksRepository: StableDiffusionHyperNetworksRepository,
) : FetchAndGetHyperNetworksUseCase {
    override fun invoke() = stableDiffusionHyperNetworksRepository.fetchAndGetHyperNetworks()
}
