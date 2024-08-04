package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.data.mappers.mapRawToDomain
import com.shifthackz.aisdv1.data.provider.ServerUrlProvider
import com.shifthackz.aisdv1.domain.datasource.StableDiffusionHyperNetworksDataSource
import com.shifthackz.aisdv1.domain.entity.StableDiffusionHyperNetwork
import com.shifthackz.aisdv1.network.api.automatic1111.Automatic1111RestApi
import com.shifthackz.aisdv1.network.api.automatic1111.Automatic1111RestApi.Companion.PATH_HYPER_NETWORKS
import com.shifthackz.aisdv1.network.model.StableDiffusionHyperNetworkRaw
import io.reactivex.rxjava3.core.Single

internal class StableDiffusionHyperNetworksRemoteDataSource(
    private val serverUrlProvider: ServerUrlProvider,
    private val api: Automatic1111RestApi,
) : StableDiffusionHyperNetworksDataSource.Remote {

    override fun fetchHyperNetworks(): Single<List<StableDiffusionHyperNetwork>> = serverUrlProvider(PATH_HYPER_NETWORKS)
        .flatMap(api::fetchHyperNetworks)
        .map(List<StableDiffusionHyperNetworkRaw>::mapRawToDomain)
}
