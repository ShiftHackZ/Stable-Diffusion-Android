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

class KtorOpenAiGenerationApi(
    private val httpClient: HttpClient,
    private val baseUrl: String,
) : OpenAiGenerationApi {

    constructor(baseUrl: String) : this(
        httpClient = createConfiguredHttpClient(),
        baseUrl = baseUrl,
    )

    override suspend fun validateBearerToken(apiKey: String) {
        httpClient.get {
            url.takeFrom(baseUrl)
            url.appendPathSegments(PATH_API_VERSION, PATH_MODELS)
            header(HttpHeaders.Authorization, "Bearer $apiKey")
        }
    }

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

    private companion object {
        const val PATH_API_VERSION = "v1"
        const val PATH_MODELS = "models"
        const val PATH_IMAGES = "images"
        const val PATH_GENERATIONS = "generations"
    }
}
