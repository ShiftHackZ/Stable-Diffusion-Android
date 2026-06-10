package com.shifthackz.aisdv1.domain.usecase.sdhypernet

import com.shifthackz.aisdv1.domain.entity.StableDiffusionHyperNetwork

interface FetchAndGetHyperNetworksUseCase {
    suspend operator fun invoke(): List<StableDiffusionHyperNetwork>
}
