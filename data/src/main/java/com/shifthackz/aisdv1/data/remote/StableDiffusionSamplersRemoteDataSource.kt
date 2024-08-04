package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.data.mappers.mapRawToDomain
import com.shifthackz.aisdv1.data.provider.ServerUrlProvider
import com.shifthackz.aisdv1.domain.datasource.StableDiffusionSamplersDataSource
import com.shifthackz.aisdv1.domain.entity.StableDiffusionSampler
import com.shifthackz.aisdv1.network.api.automatic1111.Automatic1111RestApi
import com.shifthackz.aisdv1.network.api.automatic1111.Automatic1111RestApi.Companion.PATH_SAMPLERS
import com.shifthackz.aisdv1.network.model.StableDiffusionSamplerRaw
import io.reactivex.rxjava3.core.Single

internal class StableDiffusionSamplersRemoteDataSource(
    private val serverUrlProvider: ServerUrlProvider,
    private val api: Automatic1111RestApi,
) : StableDiffusionSamplersDataSource.Remote {

    override fun fetchSamplers(): Single<List<StableDiffusionSampler>> = serverUrlProvider(PATH_SAMPLERS)
        .flatMap(api::fetchSamplers)
        .map(List<StableDiffusionSamplerRaw>::mapRawToDomain)
}