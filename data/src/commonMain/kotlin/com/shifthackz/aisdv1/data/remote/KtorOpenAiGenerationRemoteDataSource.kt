package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.data.mappers.mapOpenAiCloudToAiGenResult
import com.shifthackz.aisdv1.data.mappers.mapToOpenAiRequest
import com.shifthackz.aisdv1.domain.datasource.OpenAiGenerationDataSource
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import com.shifthackz.aisdv1.network.api.openai.OpenAiGenerationApi
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class KtorOpenAiGenerationRemoteDataSource(
    private val api: OpenAiGenerationApi,
) : OpenAiGenerationDataSource.Remote {

    override suspend fun validateApiKey(apiKey: String): Boolean = try {
        api.validateBearerToken(apiKey)
        true
    } catch (_: Throwable) {
        false
    }

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
