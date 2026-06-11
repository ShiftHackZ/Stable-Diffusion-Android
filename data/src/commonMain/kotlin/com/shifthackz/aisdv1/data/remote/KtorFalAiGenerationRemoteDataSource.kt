package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.data.mappers.encodeBase64NoWrap
import com.shifthackz.aisdv1.data.mappers.mapFalAiImageToImageResult
import com.shifthackz.aisdv1.data.mappers.mapFalAiTextToImageResult
import com.shifthackz.aisdv1.data.mappers.mapToFalAiRequest
import com.shifthackz.aisdv1.domain.datasource.FalAiGenerationDataSource
import com.shifthackz.aisdv1.domain.entity.ImageToImagePayload
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import com.shifthackz.aisdv1.network.api.falai.FalAiGenerationApi
import com.shifthackz.aisdv1.network.response.FalAiImage
import com.shifthackz.aisdv1.network.response.FalAiGenerationResponse
import com.shifthackz.aisdv1.network.response.FalAiQueueSubmitResponse
import kotlinx.coroutines.delay
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * Coordinates `KtorFalAiGenerationRemoteDataSource` behavior in the SDAI data layer.
 *
 * @author Dmitriy Moroz
 */
@OptIn(ExperimentalTime::class)
class KtorFalAiGenerationRemoteDataSource(
    /**
     * Exposes the `api` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private val api: FalAiGenerationApi,
    /**
     * Exposes the `pollIntervalMillis` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private val pollIntervalMillis: Long = DEFAULT_POLL_INTERVAL_MILLIS,
    /**
     * Exposes the `maxPollAttempts` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private val maxPollAttempts: Int = DEFAULT_MAX_POLL_ATTEMPTS,
) : FalAiGenerationDataSource.Remote {

    /**
     * Executes the `validateApiKey` step in the SDAI data layer.
     *
     * @param apiKey api key value consumed by the API.
     * @return Result produced by `validateApiKey`.
     * @author Dmitriy Moroz
     */
    override suspend fun validateApiKey(apiKey: String): Boolean = try {
        api.validateApiKey(apiKey)
        true
    } catch (_: Throwable) {
        false
    }

    /**
     * Executes the `textToImage` step in the SDAI data layer.
     *
     * @param apiKey api key value consumed by the API.
     * @param payload generation payload used by the operation.
     * @return Result produced by `textToImage`.
     * @author Dmitriy Moroz
     */
    override suspend fun textToImage(apiKey: String, payload: TextToImagePayload) =
        api.submitTextToImage(
            apiKey = apiKey,
            model = payload.falAiModel.alias,
            request = payload.mapToFalAiRequest(),
        )
            .awaitResult(apiKey)
            .toGenerationResults(payload)

    /**
     * Executes the `imageToImage` step in the SDAI data layer.
     *
     * @param apiKey api key value consumed by the API.
     * @param payload generation payload used by the operation.
     * @return Result produced by `imageToImage`.
     * @author Dmitriy Moroz
     */
    override suspend fun imageToImage(apiKey: String, payload: ImageToImagePayload) =
        api.submitImageToImage(
            apiKey = apiKey,
            model = payload.falAiModel.alias,
            request = payload.mapToFalAiRequest(),
        )
            .awaitResult(apiKey)
            .toGenerationResults(payload)

    private suspend fun FalAiQueueSubmitResponse.awaitResult(apiKey: String): FalAiGenerationResponse {
        val statusUrl = statusUrl ?: throw IllegalStateException("Fal.ai did not return status_url.")
        val responseUrl = responseUrl ?: throw IllegalStateException("Fal.ai did not return response_url.")

        repeat(maxPollAttempts) {
            val status = api.getQueueStatus(apiKey, statusUrl)
            status.error?.takeIf(String::isNotBlank)?.let { error ->
                throw IllegalStateException(error)
            }
            when (status.status) {
                STATUS_COMPLETED -> return api.getQueueResult(
                    apiKey = apiKey,
                    responseUrl = status.responseUrl ?: responseUrl,
                )

                STATUS_IN_QUEUE,
                STATUS_IN_PROGRESS,
                null,
                -> delay(pollIntervalMillis)

                else -> throw IllegalStateException("Unexpected Fal.ai queue status: ${status.status}.")
            }
        }

        throw IllegalStateException("Fal.ai queue polling timed out.")
    }

    private suspend fun FalAiGenerationResponse.toGenerationResults(payload: TextToImagePayload) =
        images
            ?.mapNotNull { image ->
                val base64 = image.toBase64() ?: return@mapNotNull null
                payload.mapFalAiTextToImageResult(
                    base64 = base64,
                    responseSeed = seed,
                    createdAtMillis = Clock.System.now().toEpochMilliseconds(),
                )
            }
            ?.takeIf { it.isNotEmpty() }
            ?: throw IllegalStateException("Fal.ai did not return generated image URL.")

    private suspend fun FalAiGenerationResponse.toGenerationResults(payload: ImageToImagePayload) =
        images
            ?.mapNotNull { image ->
                val base64 = image.toBase64() ?: return@mapNotNull null
                payload.mapFalAiImageToImageResult(
                    base64 = base64,
                    responseSeed = seed,
                    createdAtMillis = Clock.System.now().toEpochMilliseconds(),
                )
            }
            ?.takeIf { it.isNotEmpty() }
            ?: throw IllegalStateException("Fal.ai did not return generated image URL.")

    private suspend fun FalAiImage.toBase64(): String? {
        val imageUrl = url?.takeIf(String::isNotBlank) ?: return null
        return if (imageUrl.startsWith(DATA_URI_PREFIX)) {
            imageUrl.substringAfter(DATA_URI_SEPARATOR)
        } else {
            api.downloadImage(imageUrl).encodeBase64NoWrap()
        }
    }

    private companion object {
        /**
         * Exposes the `STATUS_IN_QUEUE` value used by the SDAI data layer.
         *
         * @author Dmitriy Moroz
         */
        const val STATUS_IN_QUEUE = "IN_QUEUE"
        /**
         * Exposes the `STATUS_IN_PROGRESS` value used by the SDAI data layer.
         *
         * @author Dmitriy Moroz
         */
        const val STATUS_IN_PROGRESS = "IN_PROGRESS"
        /**
         * Exposes the `STATUS_COMPLETED` value used by the SDAI data layer.
         *
         * @author Dmitriy Moroz
         */
        const val STATUS_COMPLETED = "COMPLETED"
        /**
         * Exposes the `DATA_URI_PREFIX` value used by the SDAI data layer.
         *
         * @author Dmitriy Moroz
         */
        const val DATA_URI_PREFIX = "data:"
        /**
         * Exposes the `DATA_URI_SEPARATOR` value used by the SDAI data layer.
         *
         * @author Dmitriy Moroz
         */
        const val DATA_URI_SEPARATOR = "base64,"
        /**
         * Exposes the `DEFAULT_POLL_INTERVAL_MILLIS` value used by the SDAI data layer.
         *
         * @author Dmitriy Moroz
         */
        const val DEFAULT_POLL_INTERVAL_MILLIS = 2_000L
        /**
         * Exposes the `DEFAULT_MAX_POLL_ATTEMPTS` value used by the SDAI data layer.
         *
         * @author Dmitriy Moroz
         */
        const val DEFAULT_MAX_POLL_ATTEMPTS = 300
    }
}
