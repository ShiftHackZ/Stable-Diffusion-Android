package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.data.mappers.mapStableDiffusionImageToImageResult
import com.shifthackz.aisdv1.data.mappers.mapStableDiffusionTextToImageResult
import com.shifthackz.aisdv1.data.mappers.mapToArliAiRequest
import com.shifthackz.aisdv1.domain.datasource.ArliAiGenerationDataSource
import com.shifthackz.aisdv1.domain.entity.ImageToImagePayload
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import com.shifthackz.aisdv1.network.api.arliai.ArliAiGenerationApi
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * Coordinates `KtorArliAiGenerationRemoteDataSource` behavior in the SDAI data layer.
 *
 * @author Dmitriy Moroz
 */
@OptIn(ExperimentalTime::class)
class KtorArliAiGenerationRemoteDataSource(
    private val api: ArliAiGenerationApi,
) : ArliAiGenerationDataSource.Remote {

    override suspend fun validateApiKey(apiKey: String): Boolean = try {
        api.validateApiKey(apiKey)
        true
    } catch (_: Throwable) {
        false
    }

    override suspend fun textToImage(
        apiKey: String,
        model: String,
        payload: TextToImagePayload,
    ) = (payload to api.textToImage(apiKey, payload.mapToArliAiRequest(model)))
        .mapStableDiffusionTextToImageResult(Clock.System.now().toEpochMilliseconds())

    override suspend fun imageToImage(
        apiKey: String,
        model: String,
        payload: ImageToImagePayload,
    ) = (payload to api.imageToImage(apiKey, payload.mapToArliAiRequest(model)))
        .mapStableDiffusionImageToImageResult(Clock.System.now().toEpochMilliseconds())
}
