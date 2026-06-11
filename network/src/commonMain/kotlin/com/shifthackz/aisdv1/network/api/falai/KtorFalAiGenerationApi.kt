package com.shifthackz.aisdv1.network.api.falai

import com.shifthackz.aisdv1.network.client.createConfiguredHttpClient
import com.shifthackz.aisdv1.network.request.FalAiImageToImageRequest
import com.shifthackz.aisdv1.network.request.FalAiTextToImageRequest
import com.shifthackz.aisdv1.network.response.FalAiGenerationResponse
import com.shifthackz.aisdv1.network.response.FalAiQueueStatusResponse
import com.shifthackz.aisdv1.network.response.FalAiQueueSubmitResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.takeFrom

/**
 * Coordinates `KtorFalAiGenerationApi` behavior in the SDAI network layer.
 *
 * @author Dmitriy Moroz
 */
class KtorFalAiGenerationApi(
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
     * Exposes the `queueBaseUrl` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    private val queueBaseUrl: String,
) : FalAiGenerationApi {

    /**
     * Creates a new SDAI component instance.
     *
     * @param apiBaseUrl base url value consumed by the API.
     * @param queueBaseUrl queue base url value consumed by the API.
     * @author Dmitriy Moroz
     */
    constructor(
        apiBaseUrl: String,
        queueBaseUrl: String,
    ) : this(
        httpClient = createConfiguredHttpClient(),
        apiBaseUrl = apiBaseUrl,
        queueBaseUrl = queueBaseUrl,
    )

    /**
     * Executes the `validateApiKey` step in the SDAI network layer.
     *
     * @param apiKey api key value consumed by the API.
     * @author Dmitriy Moroz
     */
    override suspend fun validateApiKey(apiKey: String) {
        httpClient.get {
            url.takeFrom("$apiBaseUrl/$PATH_V1/$PATH_MODELS")
            parameter(QUERY_LIMIT, VALIDATION_LIMIT)
            header(HttpHeaders.Authorization, apiKey.headerValue)
        }
    }

    /**
     * Executes the `submitTextToImage` step in the SDAI network layer.
     *
     * @param apiKey api key value consumed by the API.
     * @param model model value consumed by the API.
     * @param request request value consumed by the API.
     * @return Result produced by `submitTextToImage`.
     * @author Dmitriy Moroz
     */
    override suspend fun submitTextToImage(
        apiKey: String,
        model: String,
        request: FalAiTextToImageRequest,
    ): FalAiQueueSubmitResponse = httpClient
        .post {
            url.takeFrom("$queueBaseUrl/$model")
            header(HttpHeaders.Authorization, apiKey.headerValue)
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        .body()

    /**
     * Executes the `submitImageToImage` step in the SDAI network layer.
     *
     * @param apiKey api key value consumed by the API.
     * @param model model value consumed by the API.
     * @param request request value consumed by the API.
     * @return Result produced by `submitImageToImage`.
     * @author Dmitriy Moroz
     */
    override suspend fun submitImageToImage(
        apiKey: String,
        model: String,
        request: FalAiImageToImageRequest,
    ): FalAiQueueSubmitResponse = httpClient
        .post {
            url.takeFrom("$queueBaseUrl/$model")
            header(HttpHeaders.Authorization, apiKey.headerValue)
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        .body()

    /**
     * Executes the `getQueueStatus` step in the SDAI network layer.
     *
     * @param apiKey api key value consumed by the API.
     * @param statusUrl remote URL used by the operation.
     * @return Result produced by `getQueueStatus`.
     * @author Dmitriy Moroz
     */
    override suspend fun getQueueStatus(
        apiKey: String,
        statusUrl: String,
    ): FalAiQueueStatusResponse = httpClient
        .get {
            url.takeFrom(statusUrl)
            header(HttpHeaders.Authorization, apiKey.headerValue)
        }
        .body()

    /**
     * Executes the `getQueueResult` step in the SDAI network layer.
     *
     * @param apiKey api key value consumed by the API.
     * @param responseUrl remote URL used by the operation.
     * @return Result produced by `getQueueResult`.
     * @author Dmitriy Moroz
     */
    override suspend fun getQueueResult(
        apiKey: String,
        responseUrl: String,
    ): FalAiGenerationResponse = httpClient
        .get {
            url.takeFrom(responseUrl)
            header(HttpHeaders.Authorization, apiKey.headerValue)
        }
        .body()

    /**
     * Executes the `downloadImage` step in the SDAI network layer.
     *
     * @param url remote URL used by the operation.
     * @return Result produced by `downloadImage`.
     * @author Dmitriy Moroz
     */
    override suspend fun downloadImage(url: String): ByteArray = httpClient
        .get(url)
        .body()

    private val String.headerValue: String
        get() = "Key $this"

    /**
     * Provides the `companion object` singleton used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    private companion object {
        /**
         * Exposes the `PATH_V1` value used by the SDAI network layer.
         *
         * @author Dmitriy Moroz
         */
        const val PATH_V1 = "v1"
        /**
         * Exposes the `PATH_MODELS` value used by the SDAI network layer.
         *
         * @author Dmitriy Moroz
         */
        const val PATH_MODELS = "models"
        /**
         * Exposes the `QUERY_LIMIT` value used by the SDAI network layer.
         *
         * @author Dmitriy Moroz
         */
        const val QUERY_LIMIT = "limit"
        /**
         * Exposes the `VALIDATION_LIMIT` value used by the SDAI network layer.
         *
         * @author Dmitriy Moroz
         */
        const val VALIDATION_LIMIT = 1
    }
}
