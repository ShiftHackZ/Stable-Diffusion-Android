package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.data.mappers.mapRawToDomain
import com.shifthackz.aisdv1.data.provider.ServerUrlProvider
import com.shifthackz.aisdv1.domain.datasource.StableDiffusionSamplersDataSource
import com.shifthackz.aisdv1.network.api.StableDiffusionWebUiAutomaticRestApi
import com.shifthackz.aisdv1.network.api.StableDiffusionWebUiAutomaticRestApi.Companion.PATH_SAMPLERS
import com.shifthackz.aisdv1.network.model.StableDiffusionSamplerRaw

class StableDiffusionSamplersRemoteDataSource(
    private val serverUrlProvider: ServerUrlProvider,
    private val api: StableDiffusionWebUiAutomaticRestApi,
) : StableDiffusionSamplersDataSource.Remote {

    override fun fetchSamplers() = serverUrlProvider(PATH_SAMPLERS)
        .flatMap(api::fetchSamplers)
        .map(List<StableDiffusionSamplerRaw>::mapRawToDomain)
}