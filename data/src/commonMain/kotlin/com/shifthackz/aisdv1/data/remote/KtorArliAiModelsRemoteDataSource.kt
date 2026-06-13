package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.data.mappers.mapKtorRawToCheckpointDomain
import com.shifthackz.aisdv1.domain.datasource.ArliAiModelsDataSource
import com.shifthackz.aisdv1.domain.entity.StableDiffusionModel
import com.shifthackz.aisdv1.network.api.arliai.ArliAiGenerationApi

/**
 * Coordinates `KtorArliAiModelsRemoteDataSource` behavior in the SDAI data layer.
 *
 * @author Dmitriy Moroz
 */
class KtorArliAiModelsRemoteDataSource(
    /**
     * Exposes the `api` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private val api: ArliAiGenerationApi,
) : ArliAiModelsDataSource.Remote {

    /**
     * Loads SDAI data through `fetchModels`.
     *
     * @param apiKey api key value consumed by the API.
     * @return Result produced by `fetchModels`.
     * @author Dmitriy Moroz
     */
    override suspend fun fetchModels(apiKey: String): List<StableDiffusionModel> =
        api.fetchModels(apiKey).mapKtorRawToCheckpointDomain()
}
