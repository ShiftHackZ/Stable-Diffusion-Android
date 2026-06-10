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

@OptIn(ExperimentalTime::class)
class KtorStabilityAiGenerationRemoteDataSource(
    private val api: StabilityAiGenerationApi,
) : StabilityAiGenerationDataSource.Remote {

    override suspend fun validateApiKey(apiKey: String): Boolean = try {
        api.validateBearerToken(apiKey)
        true
    } catch (_: Throwable) {
        false
    }

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

    private fun <T : Any> StabilityGenerationResponse.processResponse(payload: T): Pair<T, String> {
        return artifacts?.firstOrNull()?.base64?.let { base64 ->
            payload to base64
        } ?: throw IllegalStateException("Got null data object from API.")
    }
}
