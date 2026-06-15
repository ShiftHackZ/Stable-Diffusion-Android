package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.data.mappers.mapKtorRawToCheckpointDomain
import com.shifthackz.aisdv1.domain.datasource.ArliAiModelsDataSource
import com.shifthackz.aisdv1.domain.entity.StableDiffusionModel
import com.shifthackz.aisdv1.network.api.arliai.ArliAiGenerationApi

/**
 * Loads ArliAI checkpoint metadata from the network API.
 *
 * @param api ArliAI network API used for model discovery.
 *
 * @author Dmitriy Moroz
 */
class KtorArliAiModelsRemoteDataSource(
    private val api: ArliAiGenerationApi,
) : ArliAiModelsDataSource.Remote {

    /**
     * Fetches ArliAI checkpoints and maps them into shared Stable Diffusion model metadata.
     *
     * @param apiKey ArliAI API key entered by the user.
     * @return checkpoint metadata available to the supplied key.
     *
     * @author Dmitriy Moroz
     */
    override suspend fun fetchModels(apiKey: String): List<StableDiffusionModel> =
        api.fetchModels(apiKey).mapKtorRawToCheckpointDomain()
}
