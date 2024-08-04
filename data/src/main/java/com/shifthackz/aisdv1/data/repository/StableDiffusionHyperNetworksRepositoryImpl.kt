package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.domain.datasource.StableDiffusionHyperNetworksDataSource
import com.shifthackz.aisdv1.domain.entity.StableDiffusionHyperNetwork
import com.shifthackz.aisdv1.domain.repository.StableDiffusionHyperNetworksRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

internal class StableDiffusionHyperNetworksRepositoryImpl(
    private val remoteDataSource: StableDiffusionHyperNetworksDataSource.Remote,
    private val localDataSource: StableDiffusionHyperNetworksDataSource.Local,
) : StableDiffusionHyperNetworksRepository {

    override fun fetchHyperNetworks(): Completable = remoteDataSource
        .fetchHyperNetworks()
        .flatMapCompletable(localDataSource::insertHyperNetworks)

    override fun fetchAndGetHyperNetworks(): Single<List<StableDiffusionHyperNetwork>> = fetchHyperNetworks()
        .onErrorComplete()
        .andThen(getHyperNetworks())

    override fun getHyperNetworks(): Single<List<StableDiffusionHyperNetwork>> = localDataSource.getHyperNetworks()
}
