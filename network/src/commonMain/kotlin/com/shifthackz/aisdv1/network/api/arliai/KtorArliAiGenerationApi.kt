package com.shifthackz.aisdv1.network.api.arliai

import com.shifthackz.aisdv1.network.client.createConfiguredHttpClient
import com.shifthackz.aisdv1.network.client.NetworkUsageCategory
import com.shifthackz.aisdv1.network.client.setTrackedJsonBody
import com.shifthackz.aisdv1.network.client.trackUsage
import com.shifthackz.aisdv1.network.client.trackedJsonBody
import com.shifthackz.aisdv1.network.model.KtorStableDiffusionModelRaw
import com.shifthackz.aisdv1.network.request.ArliAiImageToImageRequest
import com.shifthackz.aisdv1.network.request.ArliAiTextToImageRequest
import com.shifthackz.aisdv1.network.response.SdGenerationResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.http.HttpHeaders
import io.ktor.http.appendPathSegments
import io.ktor.http.takeFrom

/**
 * Coordinates `KtorArliAiGenerationApi` behavior in the SDAI network layer.
 *
 * @author Dmitriy Moroz
 */
class KtorArliAiGenerationApi(
    private val httpClient: HttpClient,
    private val baseUrl: String,
) : ArliAiGenerationApi {

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

    override suspend fun validateApiKey(apiKey: String) {
        fetchModels(apiKey)
    }

    override suspend fun fetchModels(apiKey: String): List<KtorStableDiffusionModelRaw> = httpClient.get {
        url.takeFrom(baseUrl)
        url.appendPathSegments(PATH_SD_API, PATH_V1, PATH_SD_MODELS)
        header(HttpHeaders.Authorization, apiKey.headerValue)
        trackUsage(NetworkUsageCategory.CONFIGS)
    }.trackedJsonBody(NetworkUsageCategory.CONFIGS)

    override suspend fun textToImage(
        apiKey: String,
        request: ArliAiTextToImageRequest,
    ): SdGenerationResponse = httpClient
        .post {
            url.takeFrom(baseUrl)
            url.appendPathSegments(PATH_SD_API, PATH_V1, PATH_TXT_TO_IMG)
            header(HttpHeaders.Authorization, apiKey.headerValue)
            setTrackedJsonBody(NetworkUsageCategory.INFERENCE, request)
        }
        .trackedJsonBody(NetworkUsageCategory.INFERENCE)

    override suspend fun imageToImage(
        apiKey: String,
        request: ArliAiImageToImageRequest,
    ): SdGenerationResponse = httpClient
        .post {
            url.takeFrom(baseUrl)
            url.appendPathSegments(PATH_SD_API, PATH_V1, PATH_IMG_TO_IMG)
            header(HttpHeaders.Authorization, apiKey.headerValue)
            setTrackedJsonBody(NetworkUsageCategory.INFERENCE, request)
        }
        .trackedJsonBody(NetworkUsageCategory.INFERENCE)

    private val String.headerValue: String
        get() = "Bearer $this"

    private companion object {
        const val PATH_SD_API = "sdapi"
        const val PATH_V1 = "v1"
        const val PATH_SD_MODELS = "sd-models"
        const val PATH_TXT_TO_IMG = "txt2img"
        const val PATH_IMG_TO_IMG = "img2img"
    }
}
