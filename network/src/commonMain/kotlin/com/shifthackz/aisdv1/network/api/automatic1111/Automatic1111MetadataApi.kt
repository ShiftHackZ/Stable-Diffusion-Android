package com.shifthackz.aisdv1.network.api.automatic1111

import com.shifthackz.aisdv1.network.auth.BasicHttpAuthorization
import com.shifthackz.aisdv1.network.model.ForgeModuleRaw
import com.shifthackz.aisdv1.network.model.KtorStableDiffusionModelRaw
import com.shifthackz.aisdv1.network.model.ServerConfigurationRaw
import com.shifthackz.aisdv1.network.model.StableDiffusionExtensionRaw
import com.shifthackz.aisdv1.network.model.StableDiffusionScriptInfoRaw
import com.shifthackz.aisdv1.network.model.StableDiffusionScriptsRaw
import com.shifthackz.aisdv1.network.model.StableDiffusionHyperNetworkRaw
import com.shifthackz.aisdv1.network.model.StableDiffusionLoraRaw
import com.shifthackz.aisdv1.network.model.StableDiffusionSamplerRaw
import com.shifthackz.aisdv1.network.response.KtorSdEmbeddingsResponse

/**
 * Defines the `Automatic1111MetadataApi` contract for the SDAI network layer.
 *
 * @author Dmitriy Moroz
 */
interface Automatic1111MetadataApi {

    /**
     * Loads SDAI data through `fetchLoras`.
     *
     * @param baseUrl base url value consumed by the API.
     * @param authorization authorization value consumed by the API.
     * @return Result produced by `fetchLoras`.
     * @author Dmitriy Moroz
     */
    suspend fun fetchLoras(
        baseUrl: String,
        authorization: BasicHttpAuthorization?,
    ): List<StableDiffusionLoraRaw>

    /**
     * Loads SDAI data through `fetchEmbeddings`.
     *
     * @param baseUrl base url value consumed by the API.
     * @param authorization authorization value consumed by the API.
     * @return Result produced by `fetchEmbeddings`.
     * @author Dmitriy Moroz
     */
    suspend fun fetchEmbeddings(
        baseUrl: String,
        authorization: BasicHttpAuthorization?,
    ): KtorSdEmbeddingsResponse

    /**
     * Loads SDAI data through `fetchHyperNetworks`.
     *
     * @param baseUrl base url value consumed by the API.
     * @param authorization authorization value consumed by the API.
     * @return Result produced by `fetchHyperNetworks`.
     * @author Dmitriy Moroz
     */
    suspend fun fetchHyperNetworks(
        baseUrl: String,
        authorization: BasicHttpAuthorization?,
    ): List<StableDiffusionHyperNetworkRaw>

    /**
     * Loads SDAI data through `fetchModels`.
     *
     * @param baseUrl base url value consumed by the API.
     * @param authorization authorization value consumed by the API.
     * @return Result produced by `fetchModels`.
     * @author Dmitriy Moroz
     */
    suspend fun fetchModels(
        baseUrl: String,
        authorization: BasicHttpAuthorization?,
    ): List<KtorStableDiffusionModelRaw>

    /**
     * Loads SDAI data through `fetchSamplers`.
     *
     * @param baseUrl base url value consumed by the API.
     * @param authorization authorization value consumed by the API.
     * @return Result produced by `fetchSamplers`.
     * @author Dmitriy Moroz
     */
    suspend fun fetchSamplers(
        baseUrl: String,
        authorization: BasicHttpAuthorization?,
    ): List<StableDiffusionSamplerRaw>

    /**
     * Loads SDAI data through `fetchForgeModules`.
     *
     * @param baseUrl base url value consumed by the API.
     * @param authorization authorization value consumed by the API.
     * @return Result produced by `fetchForgeModules`.
     * @author Dmitriy Moroz
     */
    suspend fun fetchForgeModules(
        baseUrl: String,
        authorization: BasicHttpAuthorization?,
    ): List<ForgeModuleRaw>

    /**
     * Loads SDAI data through `fetchScripts`.
     *
     * @param baseUrl base url value consumed by the API.
     * @param authorization authorization value consumed by the API.
     * @return Result produced by `fetchScripts`.
     * @author Dmitriy Moroz
     */
    suspend fun fetchScripts(
        baseUrl: String,
        authorization: BasicHttpAuthorization?,
    ): StableDiffusionScriptsRaw

    /**
     * Loads SDAI data through `fetchScriptInfo`.
     *
     * @param baseUrl base url value consumed by the API.
     * @param authorization authorization value consumed by the API.
     * @return Result produced by `fetchScriptInfo`.
     * @author Dmitriy Moroz
     */
    suspend fun fetchScriptInfo(
        baseUrl: String,
        authorization: BasicHttpAuthorization?,
    ): List<StableDiffusionScriptInfoRaw>

    /**
     * Loads SDAI data through `fetchExtensions`.
     *
     * @param baseUrl base url value consumed by the API.
     * @param authorization authorization value consumed by the API.
     * @return Result produced by `fetchExtensions`.
     * @author Dmitriy Moroz
     */
    suspend fun fetchExtensions(
        baseUrl: String,
        authorization: BasicHttpAuthorization?,
    ): List<StableDiffusionExtensionRaw>

    /**
     * Loads SDAI data through `fetchConfiguration`.
     *
     * @param baseUrl base url value consumed by the API.
     * @param authorization authorization value consumed by the API.
     * @return Result produced by `fetchConfiguration`.
     * @author Dmitriy Moroz
     */
    suspend fun fetchConfiguration(
        baseUrl: String,
        authorization: BasicHttpAuthorization?,
    ): ServerConfigurationRaw

    /**
     * Performs the SDAI side effect handled by `updateConfiguration`.
     *
     * @param baseUrl base url value consumed by the API.
     * @param authorization authorization value consumed by the API.
     * @param request request value consumed by the API.
     * @author Dmitriy Moroz
     */
    suspend fun updateConfiguration(
        baseUrl: String,
        authorization: BasicHttpAuthorization?,
        request: ServerConfigurationRaw,
    )
}
