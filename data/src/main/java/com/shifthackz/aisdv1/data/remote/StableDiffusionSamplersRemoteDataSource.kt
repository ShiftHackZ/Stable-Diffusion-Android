package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.data.mappers.mapRawToCheckpointDomain
import com.shifthackz.aisdv1.data.provider.ServerUrlProvider
import com.shifthackz.aisdv1.domain.datasource.StableDiffusionSamplersDataSource
import com.shifthackz.aisdv1.network.api.automatic1111.Automatic1111RestApi
import com.shifthackz.aisdv1.network.api.automatic1111.Automatic1111RestApi.Companion.PATH_SAMPLERS
import com.shifthackz.aisdv1.network.model.StableDiffusionSamplerRaw

internal class StableDiffusionSamplersRemoteDataSource(
    private val serverUrlProvider: ServerUrlProvider,
    private val api: Automatic1111RestApi,
) : StableDiffusionSamplersDataSource.Remote {

    override fun fetchSamplers() = serverUrlProvider(PATH_SAMPLERS)
        .flatMap(api::fetchSamplers)
        .map(List<StableDiffusionSamplerRaw>::mapRawToCheckpointDomain)
}