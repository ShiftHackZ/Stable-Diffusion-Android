package com.shifthackz.aisdv1.network.api.huggingface

import com.shifthackz.aisdv1.network.client.createConfiguredHttpClient
import com.shifthackz.aisdv1.network.client.defaultNetworkJson
import com.shifthackz.aisdv1.network.request.HuggingFaceGenerationRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.appendPathSegments
import io.ktor.http.content.TextContent
import io.ktor.http.contentType
import io.ktor.http.takeFrom
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json

/**
 * Coordinates `KtorHuggingFaceGenerationApi` behavior in the SDAI network layer.
 *
 * @author Dmitriy Moroz
 */
class KtorHuggingFaceGenerationApi(
    /**
     * Exposes the `httpClient` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    private val httpClient: HttpClient,
    /**
     * Exposes the `apiBaseUrl` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    private val apiBaseUrl: String,
    /**
     * Exposes the `inferenceBaseUrl` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    private val inferenceBaseUrl: String,
    /**
     * Exposes the `json` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    private val json: Json = defaultNetworkJson,
) : HuggingFaceGenerationApi {

    constructor(
        apiBaseUrl: String,
        inferenceBaseUrl: String,
    ) : this(
        httpClient = createConfiguredHttpClient(installContentNegotiation = false),
        apiBaseUrl = apiBaseUrl,
        inferenceBaseUrl = inferenceBaseUrl,
    )

    override suspend fun validateBearerToken(apiKey: String) {
        httpClient.get {
            url.takeFrom(apiBaseUrl)
            url.appendPathSegments(PATH_API, PATH_WHOAMI)
            header(HttpHeaders.Authorization, "Bearer $apiKey")
        }
    }

    override suspend fun generate(
        apiKey: String,
        model: String,
        request: HuggingFaceGenerationRequest,
    ): ByteArray = retryServiceUnavailable {
        httpClient
            .post {
                url.takeFrom(inferenceBaseUrl)
                url.appendPathSegments(PATH_MODELS, model)
                header(HttpHeaders.Authorization, "Bearer $apiKey")
                header(HttpHeaders.Accept, ContentType.Image.PNG.toString())
                contentType(ContentType.Application.Json)
                setBody(TextContent(json.encodeToString(request), ContentType.Application.Json))
            }
            .body()
    }

    private suspend fun <T> retryServiceUnavailable(block: suspend () -> T): T {
        while (true) {
            try {
                return block()
            } catch (t: ServerResponseException) {
                if (t.response.status != HttpStatusCode.ServiceUnavailable) {
                    throw t
                }
                delay(RETRY_DELAY_MILLIS)
            }
        }
    }

    private companion object {
        const val PATH_API = "api"
        const val PATH_WHOAMI = "whoami-v2"
        const val PATH_MODELS = "models"
        const val RETRY_DELAY_MILLIS = 20_000L
    }
}
