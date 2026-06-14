package com.shifthackz.aisdv1.network.api.swarmui

import com.shifthackz.aisdv1.network.auth.BasicHttpAuthorization
import com.shifthackz.aisdv1.network.client.NetworkUsageCategory
import com.shifthackz.aisdv1.network.client.createConfiguredHttpClient
import com.shifthackz.aisdv1.network.client.setTrackedJsonBody
import com.shifthackz.aisdv1.network.client.trackedJsonBody
import com.shifthackz.aisdv1.network.exception.SwarmUiBadSessionException
import com.shifthackz.aisdv1.network.request.SwarmUiModelsRequest
import com.shifthackz.aisdv1.network.response.KtorSwarmUiModelsResponse
import com.shifthackz.aisdv1.network.response.KtorSwarmUiSessionResponse
import io.ktor.client.HttpClient
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.basicAuth
import io.ktor.client.request.post
import io.ktor.http.HttpStatusCode
import io.ktor.http.appendPathSegments
import io.ktor.http.takeFrom

/**
 * Ktor implementation of SwarmUI model discovery counted as configuration sync traffic.
 *
 * @param httpClient Configured Ktor client used to send provider requests.
 *
 * @author Dmitriy Moroz
 */
class KtorSwarmUiModelsApi(
    /**
     * Exposes the `httpClient` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    private val httpClient: HttpClient = createConfiguredHttpClient(),
) : SwarmUiModelsApi {

    override suspend fun getNewSession(
        baseUrl: String,
        authorization: BasicHttpAuthorization?,
    ): KtorSwarmUiSessionResponse = mapSessionError {
        httpClient
            .post {
                url.takeFrom(baseUrl)
                url.appendPathSegments(PATH_API, PATH_GET_NEW_SESSION)
                applyAuthorization(authorization)
                setTrackedJsonBody(NetworkUsageCategory.CONFIGS, emptyMap<String, String>())
            }
            .trackedJsonBody(NetworkUsageCategory.CONFIGS)
    }

    override suspend fun fetchModels(
        baseUrl: String,
        request: SwarmUiModelsRequest,
        authorization: BasicHttpAuthorization?,
    ): KtorSwarmUiModelsResponse = mapSessionError {
        httpClient
            .post {
                url.takeFrom(baseUrl)
                url.appendPathSegments(PATH_API, PATH_LIST_MODELS)
                applyAuthorization(authorization)
                setTrackedJsonBody(NetworkUsageCategory.CONFIGS, request)
            }
            .trackedJsonBody(NetworkUsageCategory.CONFIGS)
    }

    private fun HttpRequestBuilder.applyAuthorization(authorization: BasicHttpAuthorization?) {
        authorization?.let { credentials ->
            basicAuth(credentials.login, credentials.password)
        }
    }

    private suspend fun <T : Any> mapSessionError(block: suspend () -> T): T = try {
        block()
    } catch (t: ClientRequestException) {
        if (t.response.status == HttpStatusCode.Unauthorized) {
            throw SwarmUiBadSessionException()
        }
        throw t
    }

    private companion object {
        const val PATH_API = "API"
        const val PATH_GET_NEW_SESSION = "GetNewSession"
        const val PATH_LIST_MODELS = "ListModels"
    }
}
