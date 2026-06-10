package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.data.mappers.mapKtorRawToHyperNetworkDomain
import com.shifthackz.aisdv1.data.mappers.mapToBasicHttpAuthorization
import com.shifthackz.aisdv1.domain.datasource.StableDiffusionHyperNetworksDataSource
import com.shifthackz.aisdv1.domain.entity.StableDiffusionHyperNetwork
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials
import com.shifthackz.aisdv1.network.api.automatic1111.Automatic1111MetadataApi

class KtorStableDiffusionHyperNetworksRemoteDataSource(
    private val api: Automatic1111MetadataApi,
) : StableDiffusionHyperNetworksDataSource.Remote {

    override suspend fun fetchHyperNetworks(
        baseUrl: String,
        credentials: AuthorizationCredentials,
    ): List<StableDiffusionHyperNetwork> = api
        .fetchHyperNetworks(
            baseUrl = baseUrl,
            authorization = credentials.mapToBasicHttpAuthorization(),
        )
        .mapKtorRawToHyperNetworkDomain()
}
