package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.data.mappers.mapStableDiffusionImageToImageResult
import com.shifthackz.aisdv1.data.mappers.mapStableDiffusionTextToImageResult
import com.shifthackz.aisdv1.data.mappers.mapToBasicHttpAuthorization
import com.shifthackz.aisdv1.data.mappers.mapToStableDiffusionRequest
import com.shifthackz.aisdv1.domain.datasource.StableDiffusionGenerationDataSource
import com.shifthackz.aisdv1.domain.entity.ImageToImagePayload
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials
import com.shifthackz.aisdv1.network.api.automatic1111.Automatic1111GenerationApi
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * Coordinates `KtorStableDiffusionGenerationRemoteDataSource` behavior in the SDAI data layer.
 *
 * @author Dmitriy Moroz
 */
@OptIn(ExperimentalTime::class)
class KtorStableDiffusionGenerationRemoteDataSource(
    /**
     * Exposes the `api` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private val api: Automatic1111GenerationApi,
) : StableDiffusionGenerationDataSource.Remote {

    /**
     * Executes the `checkAvailability` step in the SDAI data layer.
     *
     * @param baseUrl base url value consumed by the API.
     * @param credentials credentials value consumed by the API.
     * @author Dmitriy Moroz
     */
    override suspend fun checkAvailability(
        baseUrl: String,
        credentials: AuthorizationCredentials,
    ) {
        api.healthCheck(baseUrl, credentials.mapToBasicHttpAuthorization())
    }

    /**
     * Executes the `textToImage` step in the SDAI data layer.
     *
     * @param baseUrl base url value consumed by the API.
     * @param credentials credentials value consumed by the API.
     * @param payload generation payload used by the operation.
     * @author Dmitriy Moroz
     */
    override suspend fun textToImage(
        baseUrl: String,
        credentials: AuthorizationCredentials,
        payload: TextToImagePayload,
    ) = api
        .textToImage(
            baseUrl = baseUrl,
            authorization = credentials.mapToBasicHttpAuthorization(),
            request = payload.mapToStableDiffusionRequest(),
        )
        .let { response -> payload to response }
        .mapStableDiffusionTextToImageResult(
            createdAtMillis = Clock.System.now().toEpochMilliseconds(),
        )

    /**
     * Executes the `imageToImage` step in the SDAI data layer.
     *
     * @param baseUrl base url value consumed by the API.
     * @param credentials credentials value consumed by the API.
     * @param payload generation payload used by the operation.
     * @author Dmitriy Moroz
     */
    override suspend fun imageToImage(
        baseUrl: String,
        credentials: AuthorizationCredentials,
        payload: ImageToImagePayload,
    ) = api
        .imageToImage(
            baseUrl = baseUrl,
            authorization = credentials.mapToBasicHttpAuthorization(),
            request = payload.mapToStableDiffusionRequest(),
        )
        .let { response -> payload to response }
        .mapStableDiffusionImageToImageResult(
            createdAtMillis = Clock.System.now().toEpochMilliseconds(),
        )

    /**
     * Performs the SDAI side effect handled by `interruptGeneration`.
     *
     * @param baseUrl base url value consumed by the API.
     * @param credentials credentials value consumed by the API.
     * @author Dmitriy Moroz
     */
    override suspend fun interruptGeneration(
        baseUrl: String,
        credentials: AuthorizationCredentials,
    ) {
        api.interrupt(baseUrl, credentials.mapToBasicHttpAuthorization())
    }
}
