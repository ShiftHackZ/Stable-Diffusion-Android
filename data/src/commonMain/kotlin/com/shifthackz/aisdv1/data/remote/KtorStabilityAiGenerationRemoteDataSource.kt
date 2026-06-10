package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.data.mappers.decodeBase64ImageBytes
import com.shifthackz.aisdv1.data.mappers.mapStabilityImageToImageResult
import com.shifthackz.aisdv1.data.mappers.mapStabilityTextToImageResult
import com.shifthackz.aisdv1.data.mappers.mapToStabilityAiRequest
import com.shifthackz.aisdv1.domain.datasource.StabilityAiGenerationDataSource
import com.shifthackz.aisdv1.domain.entity.ImageToImagePayload
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import com.shifthackz.aisdv1.network.api.stabilityai.StabilityAiGenerationApi
import com.shifthackz.aisdv1.network.response.StabilityGenerationResponse
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * Coordinates `KtorStabilityAiGenerationRemoteDataSource` behavior in the SDAI data layer.
 *
 * @throws IllegalStateException when the delegated operation cannot complete.
 * @author Dmitriy Moroz
 */
@OptIn(ExperimentalTime::class)
class KtorStabilityAiGenerationRemoteDataSource(
    /**
     * Exposes the `api` value used by the SDAI data layer.
     *
     * @throws IllegalStateException when the delegated operation cannot complete.
     * @author Dmitriy Moroz
     */
    private val api: StabilityAiGenerationApi,
) : StabilityAiGenerationDataSource.Remote {

    /**
     * Executes the `validateApiKey` step in the SDAI data layer.
     *
     * @param apiKey api key value consumed by the API.
     * @return Result produced by `validateApiKey`.
     * @author Dmitriy Moroz
     */
    override suspend fun validateApiKey(apiKey: String): Boolean = try {
        api.validateBearerToken(apiKey)
        true
    } catch (_: Throwable) {
        false
    }

    /**
     * Executes the `textToImage` step in the SDAI data layer.
     *
     * @param apiKey api key value consumed by the API.
     * @param engineId engine id value consumed by the API.
     * @param payload generation payload used by the operation.
     * @author Dmitriy Moroz
     */
    override suspend fun textToImage(
        apiKey: String,
        engineId: String,
        payload: TextToImagePayload,
    ) = api
        .textToImage(apiKey, engineId, payload.mapToStabilityAiRequest())
        .processResponse(payload)
        .let { pair ->
            pair.mapStabilityTextToImageResult(
                createdAtMillis = Clock.System.now().toEpochMilliseconds(),
            )
        }

    /**
     * Executes the `imageToImage` step in the SDAI data layer.
     *
     * @param apiKey api key value consumed by the API.
     * @param engineId engine id value consumed by the API.
     * @param payload generation payload used by the operation.
     * @author Dmitriy Moroz
     */
    override suspend fun imageToImage(
        apiKey: String,
        engineId: String,
        payload: ImageToImagePayload,
    ) = api
        .imageToImage(
            apiKey = apiKey,
            engineId = engineId,
            imageBytes = payload.base64Image.decodeBase64ImageBytes(),
            parameters = payload.mapToStabilityAiRequest(),
        )
        .processResponse(payload)
        .let { pair ->
            pair.mapStabilityImageToImageResult(
                createdAtMillis = Clock.System.now().toEpochMilliseconds(),
            )
        }

    /**
     * Executes the `processResponse` step in the SDAI data layer.
     *
     * @param payload generation payload used by the operation.
     * @return Result produced by `processResponse`.
     * @throws IllegalStateException when the delegated operation cannot complete.
     * @author Dmitriy Moroz
     */
    private fun <T : Any> StabilityGenerationResponse.processResponse(payload: T): Pair<T, String> {
        return artifacts?.firstOrNull()?.base64?.let { base64 ->
            payload to base64
        } ?: throw IllegalStateException("Got null data object from API.")
    }
}
