package com.shifthackz.aisdv1.domain.repository

import com.shifthackz.aisdv1.domain.entity.StableDiffusionHyperNetwork
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

interface StableDiffusionHyperNetworksRepository {
    fun fetchHyperNetworks(): Completable
    fun fetchAndGetHyperNetworks(): Single<List<StableDiffusionHyperNetwork>>
    fun getHyperNetworks(): Single<List<StableDiffusionHyperNetwork>>
}
