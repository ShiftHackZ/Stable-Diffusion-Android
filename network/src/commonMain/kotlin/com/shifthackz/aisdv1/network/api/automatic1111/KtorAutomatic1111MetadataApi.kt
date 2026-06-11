package com.shifthackz.aisdv1.network.api.automatic1111

import com.shifthackz.aisdv1.network.auth.BasicHttpAuthorization
import com.shifthackz.aisdv1.network.client.createConfiguredHttpClient
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
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.basicAuth
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.appendPathSegments
import io.ktor.http.contentType
import io.ktor.http.takeFrom

/**
 * Coordinates `KtorAutomatic1111MetadataApi` behavior in the SDAI network layer.
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
            applyAuthorization(authorization)
        }
        .body()

    override suspend fun fetchEmbeddings(
        baseUrl: String,
        authorization: BasicHttpAuthorization?,
    ): KtorSdEmbeddingsResponse = httpClient
        .get {
            url.takeFrom(baseUrl)
            url.appendPathSegments(PATH_SD_API, PATH_V1, PATH_EMBEDDINGS)
            applyAuthorization(authorization)
        }
        .body()

    override suspend fun fetchHyperNetworks(
        baseUrl: String,
        authorization: BasicHttpAuthorization?,
    ): List<StableDiffusionHyperNetworkRaw> = httpClient
        .get {
            url.takeFrom(baseUrl)
            url.appendPathSegments(PATH_SD_API, PATH_V1, PATH_HYPER_NETWORKS)
            applyAuthorization(authorization)
        }
        .body()

    override suspend fun fetchModels(
        baseUrl: String,
        authorization: BasicHttpAuthorization?,
    ): List<KtorStableDiffusionModelRaw> = httpClient
        .get {
            url.takeFrom(baseUrl)
            url.appendPathSegments(PATH_SD_API, PATH_V1, PATH_SD_MODELS)
            applyAuthorization(authorization)
        }
        .body()

    override suspend fun fetchSamplers(
        baseUrl: String,
        authorization: BasicHttpAuthorization?,
    ): List<StableDiffusionSamplerRaw> = httpClient
        .get {
            url.takeFrom(baseUrl)
            url.appendPathSegments(PATH_SD_API, PATH_V1, PATH_SAMPLERS)
            applyAuthorization(authorization)
        }
        .body()

    override suspend fun fetchForgeModules(
        baseUrl: String,
        authorization: BasicHttpAuthorization?,
    ): List<ForgeModuleRaw> = httpClient
        .get {
            url.takeFrom(baseUrl)
            url.appendPathSegments(PATH_SD_API, PATH_V1, PATH_SD_MODULES)
            applyAuthorization(authorization)
        }
        .body()

    override suspend fun fetchScripts(
        baseUrl: String,
        authorization: BasicHttpAuthorization?,
    ): StableDiffusionScriptsRaw = httpClient
        .get {
            url.takeFrom(baseUrl)
            url.appendPathSegments(PATH_SD_API, PATH_V1, PATH_SCRIPTS)
            applyAuthorization(authorization)
        }
        .body()

    override suspend fun fetchScriptInfo(
        baseUrl: String,
        authorization: BasicHttpAuthorization?,
    ): List<StableDiffusionScriptInfoRaw> = httpClient
        .get {
            url.takeFrom(baseUrl)
            url.appendPathSegments(PATH_SD_API, PATH_V1, PATH_SCRIPT_INFO)
            applyAuthorization(authorization)
        }
        .body()

    override suspend fun fetchExtensions(
        baseUrl: String,
        authorization: BasicHttpAuthorization?,
    ): List<StableDiffusionExtensionRaw> = httpClient
        .get {
            url.takeFrom(baseUrl)
            url.appendPathSegments(PATH_SD_API, PATH_V1, PATH_EXTENSIONS)
            applyAuthorization(authorization)
        }
        .body()

    override suspend fun fetchConfiguration(
        baseUrl: String,
        authorization: BasicHttpAuthorization?,
    ): ServerConfigurationRaw = httpClient
        .get {
            url.takeFrom(baseUrl)
            url.appendPathSegments(PATH_SD_API, PATH_V1, PATH_OPTIONS)
            applyAuthorization(authorization)
        }
        .body()

    override suspend fun updateConfiguration(
        baseUrl: String,
        authorization: BasicHttpAuthorization?,
        request: ServerConfigurationRaw,
    ) {
        httpClient.post {
            url.takeFrom(baseUrl)
            url.appendPathSegments(PATH_SD_API, PATH_V1, PATH_OPTIONS)
            applyAuthorization(authorization)
            contentType(ContentType.Application.Json)
            setBody(request)
        }
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
