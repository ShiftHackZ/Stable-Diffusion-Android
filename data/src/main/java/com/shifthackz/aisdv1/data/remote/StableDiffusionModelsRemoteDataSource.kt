package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.data.mappers.mapRawToCheckpointDomain
import com.shifthackz.aisdv1.data.provider.ServerUrlProvider
import com.shifthackz.aisdv1.domain.datasource.StableDiffusionModelsDataSource
import com.shifthackz.aisdv1.network.api.automatic1111.Automatic1111RestApi
import com.shifthackz.aisdv1.network.api.automatic1111.Automatic1111RestApi.Companion.PATH_SD_MODELS
import com.shifthackz.aisdv1.network.model.StableDiffusionModelRaw

internal class StableDiffusionModelsRemoteDataSource(
    private val serverUrlProvider: ServerUrlProvider,
    private val api: Automatic1111RestApi,
) : StableDiffusionModelsDataSource.Remote {

    override fun fetchSdModels() = serverUrlProvider(PATH_SD_MODELS)
        .flatMap(api::fetchSdModels)
        .map(List<StableDiffusionModelRaw>::mapRawToCheckpointDomain)
}
