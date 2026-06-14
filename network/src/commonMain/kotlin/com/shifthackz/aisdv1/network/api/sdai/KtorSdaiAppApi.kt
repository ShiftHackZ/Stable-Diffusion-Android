package com.shifthackz.aisdv1.network.api.sdai

import com.shifthackz.aisdv1.network.client.createConfiguredHttpClient
import com.shifthackz.aisdv1.network.client.NetworkUsageCategory
import com.shifthackz.aisdv1.network.client.trackUsage
import com.shifthackz.aisdv1.network.client.trackedJsonBody
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

/**
 * Ktor implementation of SDAI app metadata, model catalog, and report calls.
 *
 * @param httpClient Configured Ktor client used to send SDAI service requests.
 * @param appBaseUrl Base URL for SDAI app metadata and model catalogs.
 * @param reportBaseUrl Base URL for problem report submissions.
 *
 * @author Dmitriy Moroz
 */
class KtorSdaiAppApi(
    /**
     * Exposes the `httpClient` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    private val httpClient: HttpClient,
    /**
     * Exposes the `appBaseUrl` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    private val appBaseUrl: String,
    /**
     * Exposes the `reportBaseUrl` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    private val reportBaseUrl: String,
) : SdaiAppApi {

    /**
     * Creates a new SDAI component instance.
     *
     * @param appBaseUrl app base url value consumed by the API.
     * @param reportBaseUrl report base url value consumed by the API.
     * @author Dmitriy Moroz
     */
    constructor(
        appBaseUrl: String,
        reportBaseUrl: String,
    ) : this(
        httpClient = createConfiguredHttpClient(),
        appBaseUrl = appBaseUrl,
        reportBaseUrl = reportBaseUrl,
    )

    /**
     * Loads SDAI data through `fetchSupporters`.
     *
     * @return Result produced by `fetchSupporters`.
     * @author Dmitriy Moroz
     */
    override suspend fun fetchSupporters(): List<SupporterRaw> = httpClient
        .get {
            url.takeFrom(appBaseUrl)
            url.appendPathSegments(PATH_SUPPORTERS)
        }
        .body()

    /**
     * Loads SDAI data through `fetchOnnxModels`.
     *
     * @return Result produced by `fetchOnnxModels`.
     * @author Dmitriy Moroz
     */
    override suspend fun fetchOnnxModels(): List<DownloadableModelResponse> = httpClient
        .get {
            url.takeFrom(appBaseUrl)
            url.appendPathSegments(PATH_MODELS)
            trackUsage(NetworkUsageCategory.CONFIGS)
        }
        .trackedJsonBody(NetworkUsageCategory.CONFIGS)

    /**
     * Loads SDAI data through `fetchMediaPipeModels`.
     *
     * @return Result produced by `fetchMediaPipeModels`.
     * @author Dmitriy Moroz
     */
    override suspend fun fetchMediaPipeModels(): List<DownloadableModelResponse> = httpClient
        .get {
            url.takeFrom(appBaseUrl)
            url.appendPathSegments(PATH_MEDIA_PIPE)
            trackUsage(NetworkUsageCategory.CONFIGS)
        }
        .trackedJsonBody(NetworkUsageCategory.CONFIGS)

    /**
     * Loads SDAI data through `fetchSdxlModels`.
     *
     * @return Result produced by `fetchSdxlModels`.
     * @author Dmitriy Moroz
     */
    override suspend fun fetchSdxlModels(): List<DownloadableModelResponse> = httpClient
        .get {
            url.takeFrom(appBaseUrl)
            url.appendPathSegments(PATH_SDXL)
            trackUsage(NetworkUsageCategory.CONFIGS)
        }
        .trackedJsonBody(NetworkUsageCategory.CONFIGS)

    /**
     * Loads SDAI data through `fetchCoreMlModels`.
     *
     * @return Result produced by `fetchCoreMlModels`.
     * @author Dmitriy Moroz
     */
    override suspend fun fetchCoreMlModels(): List<DownloadableModelResponse> = httpClient
        .get {
            url.takeFrom(appBaseUrl)
            url.appendPathSegments(PATH_CORE_ML)
            trackUsage(NetworkUsageCategory.CONFIGS)
        }
        .trackedJsonBody(NetworkUsageCategory.CONFIGS)

    /**
     * Executes the `postReport` step in the SDAI network layer.
     *
     * @param request request value consumed by the API.
     * @author Dmitriy Moroz
     */
    override suspend fun postReport(request: ReportRequest) {
        httpClient.post {
            url.takeFrom(reportBaseUrl)
            url.appendPathSegments(PATH_REPORT)
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    /**
     * Provides the `companion object` singleton used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    private companion object {
        /**
         * Exposes the `PATH_SUPPORTERS` value used by the SDAI network layer.
         *
         * @author Dmitriy Moroz
         */
        const val PATH_SUPPORTERS = "supporters.json"
        /**
         * Exposes the `PATH_MODELS` value used by the SDAI network layer.
         *
         * @author Dmitriy Moroz
         */
        const val PATH_MODELS = "models.json"
        /**
         * Exposes the `PATH_MEDIA_PIPE` value used by the SDAI network layer.
         *
         * @author Dmitriy Moroz
         */
        const val PATH_MEDIA_PIPE = "mediapipe.json"
        /**
         * Exposes the `PATH_SDXL` value used by the SDAI network layer.
         *
         * @author Dmitriy Moroz
         */
        const val PATH_SDXL = "sdxl.json"
        /**
         * Exposes the `PATH_CORE_ML` value used by the SDAI network layer.
         *
         * @author Dmitriy Moroz
         */
        const val PATH_CORE_ML = "coreml.json"
        /**
         * Exposes the `PATH_REPORT` value used by the SDAI network layer.
         *
         * @author Dmitriy Moroz
         */
        const val PATH_REPORT = "report"
    }
}
