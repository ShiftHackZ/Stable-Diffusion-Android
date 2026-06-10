package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.data.mappers.mapKtorRawToCheckpointDomain
import com.shifthackz.aisdv1.data.mappers.mapToBasicHttpAuthorization
import com.shifthackz.aisdv1.domain.datasource.StableDiffusionSamplersDataSource
import com.shifthackz.aisdv1.domain.entity.StableDiffusionSampler
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials
import com.shifthackz.aisdv1.network.api.automatic1111.Automatic1111MetadataApi

/**
 * Coordinates `KtorStableDiffusionSamplersRemoteDataSource` behavior in the SDAI data layer.
 *
 * @author Dmitriy Moroz
 */
class KtorStableDiffusionSamplersRemoteDataSource(
    /**
     * Exposes the `api` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private val api: Automatic1111MetadataApi,
) : StableDiffusionSamplersDataSource.Remote {

    /**
     * Loads SDAI data through `fetchSamplers`.
     *
     * @param baseUrl base url value consumed by the API.
     * @param credentials credentials value consumed by the API.
     * @return Result produced by `fetchSamplers`.
     * @author Dmitriy Moroz
     */
    override suspend fun fetchSamplers(
        baseUrl: String,
        credentials: AuthorizationCredentials,
    ): List<StableDiffusionSampler> = api
        .fetchSamplers(
            baseUrl = baseUrl,
            authorization = credentials.mapToBasicHttpAuthorization(),
        )
        .mapKtorRawToCheckpointDomain()
}
