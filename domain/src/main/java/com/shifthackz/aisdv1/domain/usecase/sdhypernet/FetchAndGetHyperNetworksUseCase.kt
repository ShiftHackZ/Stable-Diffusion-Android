package com.shifthackz.aisdv1.domain.usecase.sdhypernet

import com.shifthackz.aisdv1.domain.entity.StableDiffusionHyperNetwork
import io.reactivex.rxjava3.core.Single

interface FetchAndGetHyperNetworksUseCase {
    operator fun invoke(): Single<List<StableDiffusionHyperNetwork>>
}
