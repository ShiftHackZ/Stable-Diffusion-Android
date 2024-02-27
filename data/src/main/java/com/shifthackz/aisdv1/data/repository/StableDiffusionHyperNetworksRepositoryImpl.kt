package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.domain.datasource.StableDiffusionHyperNetworksDataSource
import com.shifthackz.aisdv1.domain.repository.StableDiffusionHyperNetworksRepository

internal class StableDiffusionHyperNetworksRepositoryImpl(
    private val remoteDataSource: StableDiffusionHyperNetworksDataSource.Remote,
    private val localDataSource: StableDiffusionHyperNetworksDataSource.Local,
) : StableDiffusionHyperNetworksRepository {

    override fun fetchHyperNetworks() = remoteDataSource
        .fetchHyperNetworks()
        .flatMapCompletable(localDataSource::insertHyperNetworks)

    override fun fetchAndGetHyperNetworks() = fetchHyperNetworks()
        .onErrorComplete()
        .andThen(getHyperNetworks())

    override fun getHyperNetworks() = localDataSource.getHyperNetworks()
}
