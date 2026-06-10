package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.data.mappers.mapOpenAiCloudToAiGenResult
import com.shifthackz.aisdv1.data.mappers.mapToOpenAiRequest
import com.shifthackz.aisdv1.domain.datasource.OpenAiGenerationDataSource
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import com.shifthackz.aisdv1.network.api.openai.OpenAiGenerationApi
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * Coordinates `KtorOpenAiGenerationRemoteDataSource` behavior in the SDAI data layer.
 *
 * @throws IllegalStateException when the delegated operation cannot complete.
 * @author Dmitriy Moroz
 */
@OptIn(ExperimentalTime::class)
class KtorOpenAiGenerationRemoteDataSource(
    /**
     * Exposes the `api` value used by the SDAI data layer.
     *
     * @throws IllegalStateException when the delegated operation cannot complete.
     * @author Dmitriy Moroz
     */
    private val api: OpenAiGenerationApi,
) : OpenAiGenerationDataSource.Remote {

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
     * @param payload generation payload used by the operation.
     * @author Dmitriy Moroz
     */
    override suspend fun textToImage(
        apiKey: String,
        payload: TextToImagePayload,
    ) = api
        .generateImage(apiKey, payload.mapToOpenAiRequest())
        .data
        ?.firstOrNull()
        ?.b64json
        ?.let { base64 ->
            (payload to base64).mapOpenAiCloudToAiGenResult(
                createdAtMillis = Clock.System.now().toEpochMilliseconds(),
            )
        }
        ?: throw IllegalStateException("Got null data object from API.")
}
