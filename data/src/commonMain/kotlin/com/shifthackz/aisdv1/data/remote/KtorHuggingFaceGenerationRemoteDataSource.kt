package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.data.mappers.encodeBase64NoWrap
import com.shifthackz.aisdv1.data.mappers.mapHuggingFaceImageToImageResult
import com.shifthackz.aisdv1.data.mappers.mapHuggingFaceTextToImageResult
import com.shifthackz.aisdv1.data.mappers.mapToHuggingFaceRequest
import com.shifthackz.aisdv1.domain.datasource.HuggingFaceGenerationDataSource
import com.shifthackz.aisdv1.domain.entity.ImageToImagePayload
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import com.shifthackz.aisdv1.network.api.huggingface.HuggingFaceGenerationApi
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class KtorHuggingFaceGenerationRemoteDataSource(
    private val api: HuggingFaceGenerationApi,
) : HuggingFaceGenerationDataSource.Remote {

    override suspend fun validateApiKey(apiKey: String): Boolean = try {
        api.validateBearerToken(apiKey)
        true
    } catch (_: Throwable) {
        false
    }

    override suspend fun textToImage(
        apiKey: String,
        modelName: String,
        payload: TextToImagePayload,
    ) = api
        .generate(apiKey, modelName, payload.mapToHuggingFaceRequest())
        .encodeBase64NoWrap()
        .let { base64 ->
            (payload to base64).mapHuggingFaceTextToImageResult(
                createdAtMillis = Clock.System.now().toEpochMilliseconds(),
            )
        }

    override suspend fun imageToImage(
        apiKey: String,
        modelName: String,
        payload: ImageToImagePayload,
    ) = api
        .generate(apiKey, modelName, payload.mapToHuggingFaceRequest())
        .encodeBase64NoWrap()
        .let { base64 ->
            (payload to base64).mapHuggingFaceImageToImageResult(
                createdAtMillis = Clock.System.now().toEpochMilliseconds(),
            )
        }
}
