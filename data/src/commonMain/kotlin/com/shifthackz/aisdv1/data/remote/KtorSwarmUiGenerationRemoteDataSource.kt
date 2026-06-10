package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.core.common.extensions.fixUrlSlashes
import com.shifthackz.aisdv1.data.mappers.encodeBase64NoWrap
import com.shifthackz.aisdv1.data.mappers.mapKtorImageToImageCloudResult
import com.shifthackz.aisdv1.data.mappers.mapKtorTextToImageCloudResult
import com.shifthackz.aisdv1.data.mappers.mapToBasicHttpAuthorization
import com.shifthackz.aisdv1.data.mappers.mapToKtorSwarmUiRequest
import com.shifthackz.aisdv1.domain.datasource.SwarmUiGenerationDataSource
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.ImageToImagePayload
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials
import com.shifthackz.aisdv1.network.api.swarmui.SwarmUiGenerationApi
import com.shifthackz.aisdv1.network.request.SwarmUiGenerationRequest
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * Coordinates `KtorSwarmUiGenerationRemoteDataSource` behavior in the SDAI data layer.
 *
 * @throws IllegalStateException when the delegated operation cannot complete.
 * @author Dmitriy Moroz
 */
class KtorSwarmUiGenerationRemoteDataSource(
    /**
     * Exposes the `api` value used by the SDAI data layer.
     *
     * @throws IllegalStateException when the delegated operation cannot complete.
     * @author Dmitriy Moroz
     */
    private val api: SwarmUiGenerationApi,
) : SwarmUiGenerationDataSource.Remote {

    /**
     * Executes the `textToImage` step in the SDAI data layer.
     *
     * @param baseUrl base url value consumed by the API.
     * @param sessionId session id value consumed by the API.
     * @param model model value consumed by the API.
     * @param credentials credentials value consumed by the API.
     * @param payload generation payload used by the operation.
     * @author Dmitriy Moroz
     */
    override suspend fun textToImage(
        baseUrl: String,
        sessionId: String,
        model: String,
        credentials: AuthorizationCredentials,
        payload: TextToImagePayload,
    ): AiGenerationResult = generate(
        baseUrl = baseUrl,
        credentials = credentials,
        payload = payload,
        request = payload.mapToKtorSwarmUiRequest(sessionId, model),
    ).let { (sourcePayload, base64) ->
        (sourcePayload to base64).mapKtorTextToImageCloudResult(currentTimeMillis())
    }

    /**
     * Executes the `imageToImage` step in the SDAI data layer.
     *
     * @param baseUrl base url value consumed by the API.
     * @param sessionId session id value consumed by the API.
     * @param model model value consumed by the API.
     * @param credentials credentials value consumed by the API.
     * @param payload generation payload used by the operation.
     * @author Dmitriy Moroz
     */
    override suspend fun imageToImage(
        baseUrl: String,
        sessionId: String,
        model: String,
        credentials: AuthorizationCredentials,
        payload: ImageToImagePayload,
    ): AiGenerationResult = generate(
        baseUrl = baseUrl,
        credentials = credentials,
        payload = payload,
        request = payload.mapToKtorSwarmUiRequest(sessionId, model),
    ).let { (_, base64) ->
        (payload to base64).mapKtorImageToImageCloudResult(currentTimeMillis())
    }

    /**
     * Executes the `generate` step in the SDAI data layer.
     *
     * @param baseUrl base url value consumed by the API.
     * @param credentials credentials value consumed by the API.
     * @param payload generation payload used by the operation.
     * @param request request value consumed by the API.
     * @return Result produced by `generate`.
     * @throws IllegalStateException when the delegated operation cannot complete.
     * @author Dmitriy Moroz
     */
    private suspend fun <T : Any> generate(
        baseUrl: String,
        credentials: AuthorizationCredentials,
        payload: T,
        request: SwarmUiGenerationRequest,
    ): Pair<T, String> {
        val authorization = credentials.mapToBasicHttpAuthorization()
        val response = api.generate(baseUrl, request, authorization)
        val imageUrl = response.images
            ?.firstOrNull()
            ?.let { endpoint -> "$baseUrl/$endpoint".fixUrlSlashes() }
            ?: throw IllegalStateException("Bad response")
        val base64 = api
            .downloadImage(imageUrl, authorization)
            .encodeBase64NoWrap()
        return payload to base64
    }

    /**
     * Executes the `currentTimeMillis` step in the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    @OptIn(ExperimentalTime::class)
    private fun currentTimeMillis(): Long = Clock.System.now().toEpochMilliseconds()
}
