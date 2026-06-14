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
 * Maps ArliAI network responses into domain generation results.
 *
 * The provider response is Automatic1111-compatible, so this data source reuses the Stable
 * Diffusion response mappers after converting domain payloads into ArliAI requests.
 *
 * @param api ArliAI network API used for validation and generation calls.
 *
 * @author Dmitriy Moroz
 */
@OptIn(ExperimentalTime::class)
class KtorArliAiGenerationRemoteDataSource(
    private val api: ArliAiGenerationApi,
) : ArliAiGenerationDataSource.Remote {

    /**
     * Treats any successful model-list request as a valid API key check.
     *
     * @param apiKey ArliAI API key entered by the user.
     * @return `true` when the provider request succeeds.
     *
     * @author Dmitriy Moroz
     */
    override suspend fun validateApiKey(apiKey: String): Boolean = try {
        api.validateApiKey(apiKey)
        true
    } catch (_: Throwable) {
        false
    }

    /**
     * Sends text-to-image generation and maps returned images with the current timestamp.
     *
     * @param apiKey ArliAI API key entered by the user.
     * @param model checkpoint name sent to ArliAI.
     * @param payload domain generation settings.
     * @return mapped generation records returned by the provider.
     *
     * @author Dmitriy Moroz
     */
    override suspend fun textToImage(
        apiKey: String,
        model: String,
        payload: TextToImagePayload,
    ) = (payload to api.textToImage(apiKey, payload.mapToArliAiRequest(model)))
        .mapStableDiffusionTextToImageResult(Clock.System.now().toEpochMilliseconds())

    /**
     * Sends image-to-image generation and maps returned images with the current timestamp.
     *
     * @param apiKey ArliAI API key entered by the user.
     * @param model checkpoint name sent to ArliAI.
     * @param payload domain generation settings and source image data.
     * @return mapped generation records returned by the provider.
     *
     * @author Dmitriy Moroz
     */
    override suspend fun imageToImage(
        apiKey: String,
        model: String,
        payload: ImageToImagePayload,
    ) = (payload to api.imageToImage(apiKey, payload.mapToArliAiRequest(model)))
        .mapStableDiffusionImageToImageResult(Clock.System.now().toEpochMilliseconds())
}
