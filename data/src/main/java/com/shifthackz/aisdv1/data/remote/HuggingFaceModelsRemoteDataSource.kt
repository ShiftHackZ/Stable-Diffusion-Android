package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.data.mappers.mapRawToCheckpointDomain
import com.shifthackz.aisdv1.domain.datasource.HuggingFaceModelsDataSource
import com.shifthackz.aisdv1.domain.entity.HuggingFaceModel
import com.shifthackz.aisdv1.network.api.sdai.HuggingFaceModelsApi
import com.shifthackz.aisdv1.network.model.HuggingFaceModelRaw

internal class HuggingFaceModelsRemoteDataSource(
    private val api: HuggingFaceModelsApi,
) : HuggingFaceModelsDataSource.Remote {

    override fun fetchHuggingFaceModels() = api
        .fetchHuggingFaceModels()
        .map(List<HuggingFaceModelRaw>::mapRawToCheckpointDomain)
        .onErrorReturn { listOf(HuggingFaceModel.default) }
}
