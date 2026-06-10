package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.data.mappers.mapKtorRawToHyperNetworkDomain
import com.shifthackz.aisdv1.data.mappers.mapToBasicHttpAuthorization
import com.shifthackz.aisdv1.domain.datasource.StableDiffusionHyperNetworksDataSource
import com.shifthackz.aisdv1.domain.entity.StableDiffusionHyperNetwork
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials
import com.shifthackz.aisdv1.network.api.automatic1111.Automatic1111MetadataApi

/**
 * Coordinates `KtorStableDiffusionHyperNetworksRemoteDataSource` behavior in the SDAI data layer.
 *
 * @author Dmitriy Moroz
 */
class KtorStableDiffusionHyperNetworksRemoteDataSource(
    /**
     * Exposes the `api` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private val api: Automatic1111MetadataApi,
) : StableDiffusionHyperNetworksDataSource.Remote {

    /**
     * Loads SDAI data through `fetchHyperNetworks`.
     *
     * @param baseUrl base url value consumed by the API.
     * @param credentials credentials value consumed by the API.
     * @return Result produced by `fetchHyperNetworks`.
     * @author Dmitriy Moroz
     */
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
