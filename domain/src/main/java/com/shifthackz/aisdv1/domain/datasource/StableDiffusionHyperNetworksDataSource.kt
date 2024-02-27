package com.shifthackz.aisdv1.domain.datasource

import com.shifthackz.aisdv1.domain.entity.StableDiffusionHyperNetwork
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

sealed interface StableDiffusionHyperNetworksDataSource {

    interface Remote : StableDiffusionHyperNetworksDataSource {
        fun fetchHyperNetworks(): Single<List<StableDiffusionHyperNetwork>>
    }

    interface Local : StableDiffusionHyperNetworksDataSource {
        fun getHyperNetworks(): Single<List<StableDiffusionHyperNetwork>>
        fun insertHyperNetworks(list: List<StableDiffusionHyperNetwork>): Completable
    }
}
