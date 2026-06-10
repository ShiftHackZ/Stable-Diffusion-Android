package com.shifthackz.aisdv1.network.api.automatic1111

import com.shifthackz.aisdv1.network.auth.BasicHttpAuthorization
import com.shifthackz.aisdv1.network.client.createConfiguredHttpClient
import com.shifthackz.aisdv1.network.request.ImageToImageRequest
import com.shifthackz.aisdv1.network.request.TextToImageRequest
import com.shifthackz.aisdv1.network.response.SdGenerationResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.basicAuth
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.appendPathSegments
import io.ktor.http.contentType
import io.ktor.http.takeFrom

/**
 * Coordinates `KtorAutomatic1111GenerationApi` behavior in the SDAI network layer.
 *
 * @author Dmitriy Moroz
 */
class KtorAutomatic1111GenerationApi(
    /**
     * Exposes the `httpClient` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    private val httpClient: HttpClient = createConfiguredHttpClient(),
) : Automatic1111GenerationApi {

    override suspend fun healthCheck(
        baseUrl: String,
        authorization: BasicHttpAuthorization?,
    ) {
        httpClient.get {
            url.takeFrom(baseUrl)
            applyAuthorization(authorization)
        }
    }

    override suspend fun textToImage(
        baseUrl: String,
        authorization: BasicHttpAuthorization?,
        request: TextToImageRequest,
    ): SdGenerationResponse = httpClient
        .post {
            url.takeFrom(baseUrl)
            url.appendPathSegments(PATH_SD_API, PATH_V1, PATH_TXT_TO_IMG)
            applyAuthorization(authorization)
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        .body()

    override suspend fun imageToImage(
        baseUrl: String,
        authorization: BasicHttpAuthorization?,
        request: ImageToImageRequest,
    ): SdGenerationResponse = httpClient
        .post {
            url.takeFrom(baseUrl)
            url.appendPathSegments(PATH_SD_API, PATH_V1, PATH_IMG_TO_IMG)
            applyAuthorization(authorization)
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        .body()

    override suspend fun interrupt(
        baseUrl: String,
        authorization: BasicHttpAuthorization?,
    ) {
        httpClient.post {
            url.takeFrom(baseUrl)
            url.appendPathSegments(PATH_SD_API, PATH_V1, PATH_INTERRUPT)
            applyAuthorization(authorization)
        }
    }

    private fun HttpRequestBuilder.applyAuthorization(authorization: BasicHttpAuthorization?) {
        authorization?.let { credentials ->
            basicAuth(credentials.login, credentials.password)
        }
    }

    private companion object {
        const val PATH_SD_API = "sdapi"
        const val PATH_V1 = "v1"
        const val PATH_TXT_TO_IMG = "txt2img"
        const val PATH_IMG_TO_IMG = "img2img"
        const val PATH_INTERRUPT = "interrupt"
    }
}
