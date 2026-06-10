package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.data.mappers.mapRawToCheckpointDomain
import com.shifthackz.aisdv1.domain.datasource.HuggingFaceModelsRemoteDataSource
import com.shifthackz.aisdv1.domain.entity.HuggingFaceModel
import com.shifthackz.aisdv1.network.api.huggingface.HuggingFaceModelsApi

/**
 * Coordinates `KtorHuggingFaceModelsRemoteDataSource` behavior in the SDAI data layer.
 *
 * @author Dmitriy Moroz
 */
class KtorHuggingFaceModelsRemoteDataSource(
    /**
     * Exposes the `api` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private val api: HuggingFaceModelsApi,
) : HuggingFaceModelsRemoteDataSource {

    /**
     * Loads SDAI data through `fetchHuggingFaceModels`.
     *
     * @return Result produced by `fetchHuggingFaceModels`.
     * @author Dmitriy Moroz
     */
    override suspend fun fetchHuggingFaceModels(): List<HuggingFaceModel> =
        api.fetchTextToImageModels().mapRawToCheckpointDomain()
}
