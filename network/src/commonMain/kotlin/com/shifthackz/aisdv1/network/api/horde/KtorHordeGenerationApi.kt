package com.shifthackz.aisdv1.network.api.horde

import com.shifthackz.aisdv1.network.client.createConfiguredHttpClient
import com.shifthackz.aisdv1.network.request.HordeGenerationAsyncRequest
import com.shifthackz.aisdv1.network.response.HordeGenerationAsyncResponse
import com.shifthackz.aisdv1.network.response.HordeGenerationCheckFullResponse
import com.shifthackz.aisdv1.network.response.HordeGenerationCheckResponse
import com.shifthackz.aisdv1.network.response.HordeUserResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.appendPathSegments
import io.ktor.http.contentType
import io.ktor.http.takeFrom

class KtorHordeGenerationApi(
    private val httpClient: HttpClient,
    private val baseUrl: String,
) : HordeGenerationApi {

    constructor(baseUrl: String) : this(
        httpClient = createConfiguredHttpClient(),
        baseUrl = baseUrl,
    )

    override suspend fun generateAsync(
        apiKey: String,
        request: HordeGenerationAsyncRequest,
    ): HordeGenerationAsyncResponse = httpClient
        .post {
            url.takeFrom(baseUrl)
            url.appendPathSegments(PATH_API, PATH_VERSION, PATH_GENERATE, PATH_ASYNC)
            hordeApiKey(apiKey)
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        .body()

    override suspend fun checkGeneration(
        apiKey: String,
        id: String,
    ): HordeGenerationCheckResponse = httpClient
        .get {
            url.takeFrom(baseUrl)
            url.appendPathSegments(PATH_API, PATH_VERSION, PATH_GENERATE, PATH_CHECK, id)
            hordeApiKey(apiKey)
        }
        .body()

    override suspend fun checkStatus(
        apiKey: String,
        id: String,
    ): HordeGenerationCheckFullResponse = httpClient
        .get {
            url.takeFrom(baseUrl)
            url.appendPathSegments(PATH_API, PATH_VERSION, PATH_GENERATE, PATH_STATUS, id)
            hordeApiKey(apiKey)
        }
        .body()

    override suspend fun checkHordeApiKey(apiKey: String): HordeUserResponse = httpClient
        .get {
            url.takeFrom(baseUrl)
            url.appendPathSegments(PATH_API, PATH_VERSION, PATH_FIND_USER)
            hordeApiKey(apiKey)
        }
        .body()

    override suspend fun cancelRequest(
        apiKey: String,
        requestId: String,
    ) {
        httpClient.delete {
            url.takeFrom(baseUrl)
            url.appendPathSegments(PATH_API, PATH_VERSION, PATH_GENERATE, PATH_STATUS, requestId)
            hordeApiKey(apiKey)
        }
    }

    override suspend fun downloadImage(url: String): ByteArray = httpClient
        .get(url)
        .body()

    private fun io.ktor.client.request.HttpRequestBuilder.hordeApiKey(apiKey: String) {
        header(HttpHeaders.UserAgent, "Stable-Diffusion-Android")
        header(HEADER_API_KEY, apiKey)
    }

    private companion object {
        const val HEADER_API_KEY = "apikey"
        const val PATH_API = "api"
        const val PATH_VERSION = "v2"
        const val PATH_GENERATE = "generate"
        const val PATH_ASYNC = "async"
        const val PATH_CHECK = "check"
        const val PATH_STATUS = "status"
        const val PATH_FIND_USER = "find_user"
    }
}
