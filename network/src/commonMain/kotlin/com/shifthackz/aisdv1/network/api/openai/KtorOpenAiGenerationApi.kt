package com.shifthackz.aisdv1.network.api.openai

import com.shifthackz.aisdv1.network.client.createConfiguredHttpClient
import com.shifthackz.aisdv1.network.request.OpenAiRequest
import com.shifthackz.aisdv1.network.response.OpenAiResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.appendPathSegments
import io.ktor.http.contentType
import io.ktor.http.takeFrom

/**
 * Coordinates `KtorOpenAiGenerationApi` behavior in the SDAI network layer.
 *
 * @author Dmitriy Moroz
 */
class KtorOpenAiGenerationApi(
    /**
     * Exposes the `httpClient` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    private val httpClient: HttpClient,
    /**
     * Exposes the `baseUrl` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    private val baseUrl: String,
) : OpenAiGenerationApi {

    /**
     * Creates a new SDAI component instance.
     *
     * @param baseUrl base url value consumed by the API.
     * @author Dmitriy Moroz
     */
    constructor(baseUrl: String) : this(
        httpClient = createConfiguredHttpClient(),
        baseUrl = baseUrl,
    )

    /**
     * Executes the `validateBearerToken` step in the SDAI network layer.
     *
     * @param apiKey api key value consumed by the API.
     * @author Dmitriy Moroz
     */
    override suspend fun validateBearerToken(apiKey: String) {
        httpClient.get {
            url.takeFrom(baseUrl)
            url.appendPathSegments(PATH_API_VERSION, PATH_MODELS)
            header(HttpHeaders.Authorization, "Bearer $apiKey")
        }
    }

    /**
     * Executes the `generateImage` step in the SDAI network layer.
     *
     * @param apiKey api key value consumed by the API.
     * @param request request value consumed by the API.
     * @return Result produced by `generateImage`.
     * @author Dmitriy Moroz
     */
    override suspend fun generateImage(
        apiKey: String,
        request: OpenAiRequest,
    ): OpenAiResponse = httpClient
        .post {
            url.takeFrom(baseUrl)
            url.appendPathSegments(PATH_API_VERSION, PATH_IMAGES, PATH_GENERATIONS)
            header(HttpHeaders.Authorization, "Bearer $apiKey")
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        .body()

    /**
     * Provides the `companion object` singleton used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    private companion object {
        /**
         * Exposes the `PATH_API_VERSION` value used by the SDAI network layer.
         *
         * @author Dmitriy Moroz
         */
        const val PATH_API_VERSION = "v1"
        /**
         * Exposes the `PATH_MODELS` value used by the SDAI network layer.
         *
         * @author Dmitriy Moroz
         */
        const val PATH_MODELS = "models"
        /**
         * Exposes the `PATH_IMAGES` value used by the SDAI network layer.
         *
         * @author Dmitriy Moroz
         */
        const val PATH_IMAGES = "images"
        /**
         * Exposes the `PATH_GENERATIONS` value used by the SDAI network layer.
         *
         * @author Dmitriy Moroz
         */
        const val PATH_GENERATIONS = "generations"
    }
}
