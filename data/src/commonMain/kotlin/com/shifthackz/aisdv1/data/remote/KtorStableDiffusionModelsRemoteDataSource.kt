package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.data.mappers.mapKtorRawToCheckpointDomain
import com.shifthackz.aisdv1.data.mappers.mapToBasicHttpAuthorization
import com.shifthackz.aisdv1.domain.datasource.StableDiffusionModelsDataSource
import com.shifthackz.aisdv1.domain.entity.StableDiffusionModel
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials
import com.shifthackz.aisdv1.network.api.automatic1111.Automatic1111MetadataApi

class KtorStableDiffusionModelsRemoteDataSource(
    private val api: Automatic1111MetadataApi,
) : StableDiffusionModelsDataSource.Remote {

    override suspend fun fetchSdModels(
        baseUrl: String,
        credentials: AuthorizationCredentials,
    ): List<StableDiffusionModel> = api
        .fetchModels(
            baseUrl = baseUrl,
            authorization = credentials.mapToBasicHttpAuthorization(),
        )
        .mapKtorRawToCheckpointDomain()
}
