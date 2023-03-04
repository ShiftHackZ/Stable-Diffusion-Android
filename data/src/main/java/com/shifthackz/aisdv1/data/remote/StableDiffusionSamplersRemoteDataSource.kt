package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.data.mappers.mapToDomain
import com.shifthackz.aisdv1.domain.datasource.StableDiffusionSamplersDataSource
import com.shifthackz.aisdv1.domain.entity.StableDiffusionSamplerDomain
import com.shifthackz.aisdv1.network.api.StableDiffusionWebUiAutomaticRestApi
import com.shifthackz.aisdv1.network.model.StableDiffusionSamplerRaw
import io.reactivex.rxjava3.core.Single

class StableDiffusionSamplersRemoteDataSource(
    private val api: StableDiffusionWebUiAutomaticRestApi,
) : StableDiffusionSamplersDataSource.Remote {

    override fun fetchSamplers(): Single<List<StableDiffusionSamplerDomain>> = api
        .fetchSamplers()
        .map(List<StableDiffusionSamplerRaw>::mapToDomain)
}