package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.data.mappers.mapRawToCheckpointDomain
import com.shifthackz.aisdv1.data.provider.ServerUrlProvider
import com.shifthackz.aisdv1.domain.datasource.StableDiffusionHyperNetworksDataSource
import com.shifthackz.aisdv1.network.api.automatic1111.Automatic1111RestApi
import com.shifthackz.aisdv1.network.api.automatic1111.Automatic1111RestApi.Companion.PATH_HYPER_NETWORKS
import com.shifthackz.aisdv1.network.model.StableDiffusionHyperNetworkRaw

internal class StableDiffusionHyperNetworksRemoteDataSource(
    private val serverUrlProvider: ServerUrlProvider,
    private val api: Automatic1111RestApi,
) : StableDiffusionHyperNetworksDataSource.Remote {

    override fun fetchHyperNetworks() = serverUrlProvider(PATH_HYPER_NETWORKS)
        .flatMap(api::fetchHyperNetworks)
        .map(List<StableDiffusionHyperNetworkRaw>::mapRawToCheckpointDomain)
}
