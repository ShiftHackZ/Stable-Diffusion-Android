package com.shifthackz.aisdv1.domain.usecase.sdhypernet

import com.shifthackz.aisdv1.domain.entity.StableDiffusionHyperNetwork
import com.shifthackz.aisdv1.domain.repository.StableDiffusionHyperNetworksRepository
import io.reactivex.rxjava3.core.Single

internal class FetchAndGetHyperNetworksUseCaseImpl(
    private val stableDiffusionHyperNetworksRepository: StableDiffusionHyperNetworksRepository,
) : FetchAndGetHyperNetworksUseCase {
    override fun invoke(): Single<List<StableDiffusionHyperNetwork>> = stableDiffusionHyperNetworksRepository.fetchAndGetHyperNetworks()
}
