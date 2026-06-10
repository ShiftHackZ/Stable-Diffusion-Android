package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.data.mappers.mapRawToCheckpointDomain
import com.shifthackz.aisdv1.domain.datasource.HuggingFaceModelsRemoteDataSource
import com.shifthackz.aisdv1.domain.entity.HuggingFaceModel
import com.shifthackz.aisdv1.network.api.huggingface.HuggingFaceModelsApi

class KtorHuggingFaceModelsRemoteDataSource(
    private val api: HuggingFaceModelsApi,
) : HuggingFaceModelsRemoteDataSource {

    override suspend fun fetchHuggingFaceModels(): List<HuggingFaceModel> =
        api.fetchTextToImageModels().mapRawToCheckpointDomain()
}
