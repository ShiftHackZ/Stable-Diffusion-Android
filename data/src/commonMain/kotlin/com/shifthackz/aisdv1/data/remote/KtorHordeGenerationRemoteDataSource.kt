package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.data.mappers.encodeBase64NoWrap
import com.shifthackz.aisdv1.data.mappers.mapHordeImageToImageResult
import com.shifthackz.aisdv1.data.mappers.mapHordeTextToImageResult
import com.shifthackz.aisdv1.data.mappers.mapToHordeRequest
import com.shifthackz.aisdv1.domain.datasource.HordeGenerationDataSource
import com.shifthackz.aisdv1.domain.entity.HordeProcessStatus
import com.shifthackz.aisdv1.domain.entity.ImageToImagePayload
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import com.shifthackz.aisdv1.network.api.horde.HordeGenerationApi
import com.shifthackz.aisdv1.network.request.HordeGenerationAsyncRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * Coordinates `KtorHordeGenerationRemoteDataSource` behavior in the SDAI data layer.
 *
 * @throws IllegalStateException when the delegated operation cannot complete.
 * @author Dmitriy Moroz
 */
@OptIn(ExperimentalTime::class)
class KtorHordeGenerationRemoteDataSource(
    /**
     * Exposes the `api` value used by the SDAI data layer.
     *
     * @throws IllegalStateException when the delegated operation cannot complete.
     * @author Dmitriy Moroz
     */
    private val api: HordeGenerationApi,
    /**
     * Exposes the `statusSource` value used by the SDAI data layer.
     *
     * @throws IllegalStateException when the delegated operation cannot complete.
     * @author Dmitriy Moroz
     */
    private val statusSource: HordeGenerationDataSource.StatusSource,
) : HordeGenerationDataSource.Remote {

    /**
     * Executes the `validateApiKey` step in the SDAI data layer.
     *
     * @param apiKey api key value consumed by the API.
     * @return Result produced by `validateApiKey`.
     * @author Dmitriy Moroz
     */
    override suspend fun validateApiKey(apiKey: String): Boolean = try {
        api.checkHordeApiKey(apiKey).id != null
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
    ) = executeRequestChain(apiKey, payload.mapToHordeRequest())
        .let { base64 -> payload to base64 }
        .mapHordeTextToImageResult(
            createdAtMillis = Clock.System.now().toEpochMilliseconds(),
        )

    /**
     * Executes the `imageToImage` step in the SDAI data layer.
     *
     * @param apiKey api key value consumed by the API.
     * @param payload generation payload used by the operation.
     * @author Dmitriy Moroz
     */
    override suspend fun imageToImage(
        apiKey: String,
        payload: ImageToImagePayload,
    ) = executeRequestChain(apiKey, payload.mapToHordeRequest())
        .let { base64 -> payload to base64 }
        .mapHordeImageToImageResult(
            createdAtMillis = Clock.System.now().toEpochMilliseconds(),
        )

    /**
     * Performs the SDAI side effect handled by `interruptGeneration`.
     *
     * @param apiKey api key value consumed by the API.
     * @throws IllegalStateException when the delegated operation cannot complete.
     * @author Dmitriy Moroz
     */
    override suspend fun interruptGeneration(apiKey: String) {
        val id = statusSource.id ?: throw IllegalStateException("No cached request id")
        api.cancelRequest(apiKey, id)
    }

    /**
     * Executes the `executeRequestChain` step in the SDAI data layer.
     *
     * @param apiKey api key value consumed by the API.
     * @param request request value consumed by the API.
     * @return Result produced by `executeRequestChain`.
     * @author Dmitriy Moroz
     */
    private suspend fun executeRequestChain(
        apiKey: String,
        request: HordeGenerationAsyncRequest,
    ): String {
        val id = api.generateAsync(apiKey, request).id
            ?: throw Throwable("Horde returned null generation id")
        statusSource.id = id

        while (true) {
            val pingResponse = api.checkGeneration(apiKey, id)
            if (pingResponse.isPossible == false) {
                throw Throwable("Response is not possible")
            }
            if (pingResponse.done == true) {
                break
            }
            statusSource.update(
                HordeProcessStatus(
                    waitTimeSeconds = pingResponse.waitTime ?: 0,
                    queuePosition = pingResponse.queuePosition,
                ),
            )
            delay(HORDE_SOCKET_PING_TIME_MILLIS)
        }

        val imageUrl = api
            .checkStatus(apiKey, id)
            .generations
            ?.firstOrNull()
            ?.img
            ?: throw Throwable("Error extracting image")
        return api.downloadImage(imageUrl).encodeBase64NoWrap()
    }

    /**
     * Provides the `companion object` singleton used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private companion object {
        /**
         * Exposes the `HORDE_SOCKET_PING_TIME_MILLIS` value used by the SDAI data layer.
         *
         * @author Dmitriy Moroz
         */
        const val HORDE_SOCKET_PING_TIME_MILLIS = 10_000L
    }
}

/**
 * Coordinates `HordeStatusSource` behavior in the SDAI data layer.
 *
 * @author Dmitriy Moroz
 */
class HordeStatusSource : HordeGenerationDataSource.StatusSource {
    /**
     * Exposes the `processStatus` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private val processStatus = MutableSharedFlow<HordeProcessStatus>(
        extraBufferCapacity = 64,
    )
    /**
     * Exposes the `_id` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private var _id: String? = null

    /**
     * Exposes the `id` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    override var id: String?
        get() = _id
        set(value) {
            _id = value
        }

    /**
     * Loads SDAI data through `observe`.
     *
     * @return Result produced by `observe`.
     * @author Dmitriy Moroz
     */
    override fun observe(): Flow<HordeProcessStatus> = processStatus

    /**
     * Performs the SDAI side effect handled by `update`.
     *
     * @param status status value consumed by the API.
     * @author Dmitriy Moroz
     */
    override fun update(status: HordeProcessStatus) {
        processStatus.tryEmit(status)
    }
}
