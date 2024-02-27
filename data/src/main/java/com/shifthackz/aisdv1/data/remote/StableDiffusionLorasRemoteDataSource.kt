package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.data.mappers.mapToDomain
import com.shifthackz.aisdv1.data.provider.ServerUrlProvider
import com.shifthackz.aisdv1.domain.datasource.StableDiffusionLorasDataSource
import com.shifthackz.aisdv1.domain.entity.StableDiffusionLora
import com.shifthackz.aisdv1.network.api.automatic1111.Automatic1111RestApi
import com.shifthackz.aisdv1.network.api.automatic1111.Automatic1111RestApi.Companion.PATH_LORAS
import com.shifthackz.aisdv1.network.model.StableDiffusionLoraRaw
import io.reactivex.rxjava3.core.Single

internal class StableDiffusionLorasRemoteDataSource(
    private val serverUrlProvider: ServerUrlProvider,
    private val api: Automatic1111RestApi,
) : StableDiffusionLorasDataSource.Remote {

    override fun fetchLoras(): Single<List<StableDiffusionLora>> = serverUrlProvider(PATH_LORAS)
        .flatMap(api::fetchLoras)
        .map(List<StableDiffusionLoraRaw>::mapToDomain)
}
