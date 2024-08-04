package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.data.mappers.mapRawToDomain
import com.shifthackz.aisdv1.domain.datasource.HuggingFaceModelsDataSource
import com.shifthackz.aisdv1.domain.entity.HuggingFaceModel
import com.shifthackz.aisdv1.network.api.sdai.HuggingFaceModelsApi
import com.shifthackz.aisdv1.network.model.HuggingFaceModelRaw
import io.reactivex.rxjava3.core.Single

internal class HuggingFaceModelsRemoteDataSource(
    private val api: HuggingFaceModelsApi,
) : HuggingFaceModelsDataSource.Remote {

    override fun fetchHuggingFaceModels(): Single<List<HuggingFaceModel>> = api
        .fetchHuggingFaceModels()
        .map(List<HuggingFaceModelRaw>::mapRawToDomain)
        .onErrorReturn { listOf(HuggingFaceModel.default) }
}
