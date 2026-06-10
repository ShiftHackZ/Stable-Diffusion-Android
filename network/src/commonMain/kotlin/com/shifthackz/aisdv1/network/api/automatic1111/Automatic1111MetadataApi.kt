package com.shifthackz.aisdv1.network.api.automatic1111

import com.shifthackz.aisdv1.network.auth.BasicHttpAuthorization
import com.shifthackz.aisdv1.network.model.ServerConfigurationRaw
import com.shifthackz.aisdv1.network.model.StableDiffusionHyperNetworkRaw
import com.shifthackz.aisdv1.network.model.StableDiffusionLoraRaw
import com.shifthackz.aisdv1.network.model.KtorStableDiffusionModelRaw
import com.shifthackz.aisdv1.network.model.StableDiffusionSamplerRaw
import com.shifthackz.aisdv1.network.response.KtorSdEmbeddingsResponse

interface Automatic1111MetadataApi {

    suspend fun fetchLoras(
        baseUrl: String,
        authorization: BasicHttpAuthorization?,
    ): List<StableDiffusionLoraRaw>

    suspend fun fetchEmbeddings(
        baseUrl: String,
        authorization: BasicHttpAuthorization?,
    ): KtorSdEmbeddingsResponse

    suspend fun fetchHyperNetworks(
        baseUrl: String,
        authorization: BasicHttpAuthorization?,
    ): List<StableDiffusionHyperNetworkRaw>

    suspend fun fetchModels(
        baseUrl: String,
        authorization: BasicHttpAuthorization?,
    ): List<KtorStableDiffusionModelRaw>

    suspend fun fetchSamplers(
        baseUrl: String,
        authorization: BasicHttpAuthorization?,
    ): List<StableDiffusionSamplerRaw>

    suspend fun fetchConfiguration(
        baseUrl: String,
        authorization: BasicHttpAuthorization?,
    ): ServerConfigurationRaw

    suspend fun updateConfiguration(
        baseUrl: String,
        authorization: BasicHttpAuthorization?,
        request: ServerConfigurationRaw,
    )
}
