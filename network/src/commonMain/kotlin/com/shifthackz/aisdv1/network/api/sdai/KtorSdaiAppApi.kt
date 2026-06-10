package com.shifthackz.aisdv1.network.api.sdai

import com.shifthackz.aisdv1.network.client.createConfiguredHttpClient
import com.shifthackz.aisdv1.network.model.SupporterRaw
import com.shifthackz.aisdv1.network.request.ReportRequest
import com.shifthackz.aisdv1.network.response.DownloadableModelResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.appendPathSegments
import io.ktor.http.contentType
import io.ktor.http.takeFrom

class KtorSdaiAppApi(
    private val httpClient: HttpClient,
    private val appBaseUrl: String,
    private val reportBaseUrl: String,
) : SdaiAppApi {

    constructor(
        appBaseUrl: String,
        reportBaseUrl: String,
    ) : this(
        httpClient = createConfiguredHttpClient(),
        appBaseUrl = appBaseUrl,
        reportBaseUrl = reportBaseUrl,
    )

    override suspend fun fetchSupporters(): List<SupporterRaw> = httpClient
        .get {
            url.takeFrom(appBaseUrl)
            url.appendPathSegments(PATH_SUPPORTERS)
        }
        .body()

    override suspend fun fetchOnnxModels(): List<DownloadableModelResponse> = httpClient
        .get {
            url.takeFrom(appBaseUrl)
            url.appendPathSegments(PATH_MODELS)
        }
        .body()

    override suspend fun fetchMediaPipeModels(): List<DownloadableModelResponse> = httpClient
        .get {
            url.takeFrom(appBaseUrl)
            url.appendPathSegments(PATH_MEDIA_PIPE)
        }
        .body()

    override suspend fun postReport(request: ReportRequest) {
        httpClient.post {
            url.takeFrom(reportBaseUrl)
            url.appendPathSegments(PATH_REPORT)
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    private companion object {
        const val PATH_SUPPORTERS = "supporters.json"
        const val PATH_MODELS = "models.json"
        const val PATH_MEDIA_PIPE = "mediapipe.json"
        const val PATH_REPORT = "report"
    }
}
