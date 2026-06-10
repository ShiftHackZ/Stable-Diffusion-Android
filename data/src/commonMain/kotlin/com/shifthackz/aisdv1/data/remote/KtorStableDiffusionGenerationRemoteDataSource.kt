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

@OptIn(ExperimentalTime::class)
class KtorStableDiffusionGenerationRemoteDataSource(
    private val api: Automatic1111GenerationApi,
) : StableDiffusionGenerationDataSource.Remote {

    override suspend fun checkAvailability(
        baseUrl: String,
        credentials: AuthorizationCredentials,
    ) {
        api.healthCheck(baseUrl, credentials.mapToBasicHttpAuthorization())
    }

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

    override suspend fun interruptGeneration(
        baseUrl: String,
        credentials: AuthorizationCredentials,
    ) {
        api.interrupt(baseUrl, credentials.mapToBasicHttpAuthorization())
    }
}
