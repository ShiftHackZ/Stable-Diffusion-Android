package com.shifthackz.aisdv1.network.api.stabilityai

import com.shifthackz.aisdv1.network.client.createConfiguredHttpClient
import com.shifthackz.aisdv1.network.client.defaultNetworkJson
import com.shifthackz.aisdv1.network.model.StabilityAiEngineRaw
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.appendPathSegments
import io.ktor.http.takeFrom
import kotlinx.serialization.json.Json

class KtorStabilityAiEnginesApi(
    private val httpClient: HttpClient,
    private val baseUrl: String,
    private val json: Json = defaultNetworkJson,
) : StabilityAiEnginesApi {

    constructor(baseUrl: String) : this(
        httpClient = createConfiguredHttpClient(installContentNegotiation = false),
        baseUrl = baseUrl,
    )

    override suspend fun fetchEngines(apiKey: String): List<StabilityAiEngineRaw> = httpClient
        .get {
            url.takeFrom(baseUrl)
            url.appendPathSegments(PATH_API_VERSION, PATH_ENGINES, PATH_LIST)
            header(HttpHeaders.Authorization, "Bearer $apiKey")
            header(HttpHeaders.Accept, ContentType.Application.Json.toString())
        }
        .bodyAsText()
        .let { json.decodeFromString<List<StabilityAiEngineRaw>>(it) }

    private companion object {
        const val PATH_API_VERSION = "v1"
        const val PATH_ENGINES = "engines"
        const val PATH_LIST = "list"
    }
}
