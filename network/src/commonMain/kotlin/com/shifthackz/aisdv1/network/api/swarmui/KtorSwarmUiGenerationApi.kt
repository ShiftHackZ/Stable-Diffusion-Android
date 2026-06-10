package com.shifthackz.aisdv1.network.api.swarmui

import com.shifthackz.aisdv1.network.auth.BasicHttpAuthorization
import com.shifthackz.aisdv1.network.client.createConfiguredHttpClient
import com.shifthackz.aisdv1.network.exception.SwarmUiBadSessionException
import com.shifthackz.aisdv1.network.request.SwarmUiGenerationRequest
import com.shifthackz.aisdv1.network.response.KtorSwarmUiGenerationResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.basicAuth
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.appendPathSegments
import io.ktor.http.contentType
import io.ktor.http.takeFrom

/**
 * Coordinates `KtorSwarmUiGenerationApi` behavior in the SDAI network layer.
 *
 * @author Dmitriy Moroz
 */
class KtorSwarmUiGenerationApi(
    /**
     * Exposes the `httpClient` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    private val httpClient: HttpClient = createConfiguredHttpClient(),
) : SwarmUiGenerationApi {

    override suspend fun generate(
        baseUrl: String,
        request: SwarmUiGenerationRequest,
        authorization: BasicHttpAuthorization?,
    ): KtorSwarmUiGenerationResponse = mapSessionError {
        httpClient
            .post {
                url.takeFrom(baseUrl)
                url.appendPathSegments(PATH_API, PATH_GENERATE)
                applyAuthorization(authorization)
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            .body()
    }

    override suspend fun downloadImage(
        url: String,
        authorization: BasicHttpAuthorization?,
    ): ByteArray = httpClient
        .get {
            this.url.takeFrom(url)
            applyAuthorization(authorization)
        }
        .body()

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
        const val PATH_GENERATE = "GenerateText2Image"
    }
}
