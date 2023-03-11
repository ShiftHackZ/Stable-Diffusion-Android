package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.data.mappers.mapRawToDomain
import com.shifthackz.aisdv1.data.provider.ServerUrlProvider
import com.shifthackz.aisdv1.domain.datasource.StableDiffusionModelsDataSource
import com.shifthackz.aisdv1.network.api.StableDiffusionWebUiAutomaticRestApi
import com.shifthackz.aisdv1.network.api.StableDiffusionWebUiAutomaticRestApi.Companion.PATH_SD_MODELS
import com.shifthackz.aisdv1.network.model.StableDiffusionModelRaw

class StableDiffusionModelsRemoteDataSource(
    private val serverUrlProvider: ServerUrlProvider,
    private val api: StableDiffusionWebUiAutomaticRestApi,
) : StableDiffusionModelsDataSource.Remote {

    override fun fetchSdModels() = serverUrlProvider(PATH_SD_MODELS)
        .flatMap(api::fetchSdModels)
        .map(List<StableDiffusionModelRaw>::mapRawToDomain)
}
