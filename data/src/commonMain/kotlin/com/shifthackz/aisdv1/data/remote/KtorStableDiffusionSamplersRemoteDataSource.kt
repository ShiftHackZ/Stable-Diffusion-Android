package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.data.mappers.mapKtorRawToCheckpointDomain
import com.shifthackz.aisdv1.data.mappers.mapToBasicHttpAuthorization
import com.shifthackz.aisdv1.domain.datasource.StableDiffusionSamplersDataSource
import com.shifthackz.aisdv1.domain.entity.StableDiffusionSampler
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials
import com.shifthackz.aisdv1.network.api.automatic1111.Automatic1111MetadataApi

class KtorStableDiffusionSamplersRemoteDataSource(
    private val api: Automatic1111MetadataApi,
) : StableDiffusionSamplersDataSource.Remote {

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
