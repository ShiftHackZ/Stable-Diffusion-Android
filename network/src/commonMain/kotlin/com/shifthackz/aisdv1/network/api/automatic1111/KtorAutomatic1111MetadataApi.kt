package com.shifthackz.aisdv1.network.api.automatic1111

import com.shifthackz.aisdv1.network.auth.BasicHttpAuthorization
import com.shifthackz.aisdv1.network.client.NetworkUsageCategory
import com.shifthackz.aisdv1.network.client.createConfiguredHttpClient
import com.shifthackz.aisdv1.network.client.setTrackedJsonBody
import com.shifthackz.aisdv1.network.client.trackUsage
import com.shifthackz.aisdv1.network.client.trackedJsonBody
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
import io.ktor.client.HttpClient
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.basicAuth
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.http.appendPathSegments
import io.ktor.http.takeFrom

/**
 * Ktor implementation of Automatic1111 metadata calls counted as configuration sync traffic.
 *
 * @param httpClient Configured Ktor client used to send provider requests.
 *
 * @author Dmitriy Moroz
 */
class KtorAutomatic1111MetadataApi(
    /**
     * Exposes the `httpClient` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    private val httpClient: HttpClient = createConfiguredHttpClient(),
) : Automatic1111MetadataApi {

    override suspend fun fetchLoras(
        baseUrl: String,
        authorization: BasicHttpAuthorization?,
    ): List<StableDiffusionLoraRaw> = httpClient
        .get {
            url.takeFrom(baseUrl)
            url.appendPathSegments(PATH_SD_API, PATH_V1, PATH_LORAS)
            applyMetadataRequest(authorization)
        }
        .trackedJsonBody(NetworkUsageCategory.CONFIGS)

    override suspend fun fetchEmbeddings(
        baseUrl: String,
        authorization: BasicHttpAuthorization?,
    ): KtorSdEmbeddingsResponse = httpClient
        .get {
            url.takeFrom(baseUrl)
            url.appendPathSegments(PATH_SD_API, PATH_V1, PATH_EMBEDDINGS)
            applyMetadataRequest(authorization)
        }
        .trackedJsonBody(NetworkUsageCategory.CONFIGS)

    override suspend fun fetchHyperNetworks(
        baseUrl: String,
        authorization: BasicHttpAuthorization?,
    ): List<StableDiffusionHyperNetworkRaw> = httpClient
        .get {
            url.takeFrom(baseUrl)
            url.appendPathSegments(PATH_SD_API, PATH_V1, PATH_HYPER_NETWORKS)
            applyMetadataRequest(authorization)
        }
        .trackedJsonBody(NetworkUsageCategory.CONFIGS)

    override suspend fun fetchModels(
        baseUrl: String,
        authorization: BasicHttpAuthorization?,
    ): List<KtorStableDiffusionModelRaw> = httpClient
        .get {
            url.takeFrom(baseUrl)
            url.appendPathSegments(PATH_SD_API, PATH_V1, PATH_SD_MODELS)
            applyMetadataRequest(authorization)
        }
        .trackedJsonBody(NetworkUsageCategory.CONFIGS)

    override suspend fun fetchSamplers(
        baseUrl: String,
        authorization: BasicHttpAuthorization?,
    ): List<StableDiffusionSamplerRaw> = httpClient
        .get {
            url.takeFrom(baseUrl)
            url.appendPathSegments(PATH_SD_API, PATH_V1, PATH_SAMPLERS)
            applyMetadataRequest(authorization)
        }
        .trackedJsonBody(NetworkUsageCategory.CONFIGS)

    override suspend fun fetchForgeModules(
        baseUrl: String,
        authorization: BasicHttpAuthorization?,
    ): List<ForgeModuleRaw> = httpClient
        .get {
            url.takeFrom(baseUrl)
            url.appendPathSegments(PATH_SD_API, PATH_V1, PATH_SD_MODULES)
            applyMetadataRequest(authorization)
        }
        .trackedJsonBody(NetworkUsageCategory.CONFIGS)

    override suspend fun fetchScripts(
        baseUrl: String,
        authorization: BasicHttpAuthorization?,
    ): StableDiffusionScriptsRaw = httpClient
        .get {
            url.takeFrom(baseUrl)
            url.appendPathSegments(PATH_SD_API, PATH_V1, PATH_SCRIPTS)
            applyMetadataRequest(authorization)
        }
        .trackedJsonBody(NetworkUsageCategory.CONFIGS)

    override suspend fun fetchScriptInfo(
        baseUrl: String,
        authorization: BasicHttpAuthorization?,
    ): List<StableDiffusionScriptInfoRaw> = httpClient
        .get {
            url.takeFrom(baseUrl)
            url.appendPathSegments(PATH_SD_API, PATH_V1, PATH_SCRIPT_INFO)
            applyMetadataRequest(authorization)
        }
        .trackedJsonBody(NetworkUsageCategory.CONFIGS)

    override suspend fun fetchExtensions(
        baseUrl: String,
        authorization: BasicHttpAuthorization?,
    ): List<StableDiffusionExtensionRaw> = httpClient
        .get {
            url.takeFrom(baseUrl)
            url.appendPathSegments(PATH_SD_API, PATH_V1, PATH_EXTENSIONS)
            applyMetadataRequest(authorization)
        }
        .trackedJsonBody(NetworkUsageCategory.CONFIGS)

    override suspend fun fetchConfiguration(
        baseUrl: String,
        authorization: BasicHttpAuthorization?,
    ): ServerConfigurationRaw = httpClient
        .get {
            url.takeFrom(baseUrl)
            url.appendPathSegments(PATH_SD_API, PATH_V1, PATH_OPTIONS)
            applyMetadataRequest(authorization)
        }
        .trackedJsonBody(NetworkUsageCategory.CONFIGS)

    override suspend fun updateConfiguration(
        baseUrl: String,
        authorization: BasicHttpAuthorization?,
        request: ServerConfigurationRaw,
    ) {
        httpClient.post {
            url.takeFrom(baseUrl)
            url.appendPathSegments(PATH_SD_API, PATH_V1, PATH_OPTIONS)
            applyMetadataRequest(authorization)
            setTrackedJsonBody(NetworkUsageCategory.CONFIGS, request)
        }
    }

    private fun HttpRequestBuilder.applyMetadataRequest(authorization: BasicHttpAuthorization?) {
        trackUsage(NetworkUsageCategory.CONFIGS)
        applyAuthorization(authorization)
    }

    private fun HttpRequestBuilder.applyAuthorization(authorization: BasicHttpAuthorization?) {
        authorization?.let { credentials ->
            basicAuth(credentials.login, credentials.password)
        }
    }

    private companion object {
        const val PATH_SD_API = "sdapi"
        const val PATH_V1 = "v1"
        const val PATH_LORAS = "loras"
        const val PATH_EMBEDDINGS = "embeddings"
        const val PATH_HYPER_NETWORKS = "hypernetworks"
        const val PATH_SD_MODELS = "sd-models"
        const val PATH_SAMPLERS = "samplers"
        const val PATH_SD_MODULES = "sd-modules"
        const val PATH_SCRIPTS = "scripts"
        const val PATH_SCRIPT_INFO = "script-info"
        const val PATH_EXTENSIONS = "extensions"
        const val PATH_OPTIONS = "options"
    }
}
