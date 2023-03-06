package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.data.mappers.mapRawToDomain
import com.shifthackz.aisdv1.domain.datasource.StableDiffusionModelsDataSource
import com.shifthackz.aisdv1.domain.entity.StableDiffusionModel
import com.shifthackz.aisdv1.network.api.StableDiffusionWebUiAutomaticRestApi
import com.shifthackz.aisdv1.network.model.StableDiffusionModelRaw
import io.reactivex.rxjava3.core.Single

class StableDiffusionModelsRemoteDataSource(
    private val api: StableDiffusionWebUiAutomaticRestApi,
) : StableDiffusionModelsDataSource.Remote {

    override fun fetchSdModels(): Single<List<StableDiffusionModel>> = api
        .fetchSdModels()
        .map(List<StableDiffusionModelRaw>::mapRawToDomain)
}
