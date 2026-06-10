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

@OptIn(ExperimentalTime::class)
class KtorHordeGenerationRemoteDataSource(
    private val api: HordeGenerationApi,
    private val statusSource: HordeGenerationDataSource.StatusSource,
) : HordeGenerationDataSource.Remote {

    override suspend fun validateApiKey(apiKey: String): Boolean = try {
        api.checkHordeApiKey(apiKey).id != null
    } catch (_: Throwable) {
        false
    }

    override suspend fun textToImage(
        apiKey: String,
        payload: TextToImagePayload,
    ) = executeRequestChain(apiKey, payload.mapToHordeRequest())
        .let { base64 -> payload to base64 }
        .mapHordeTextToImageResult(
            createdAtMillis = Clock.System.now().toEpochMilliseconds(),
        )

    override suspend fun imageToImage(
        apiKey: String,
        payload: ImageToImagePayload,
    ) = executeRequestChain(apiKey, payload.mapToHordeRequest())
        .let { base64 -> payload to base64 }
        .mapHordeImageToImageResult(
            createdAtMillis = Clock.System.now().toEpochMilliseconds(),
        )

    override suspend fun interruptGeneration(apiKey: String) {
        val id = statusSource.id ?: throw IllegalStateException("No cached request id")
        api.cancelRequest(apiKey, id)
    }

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

    private companion object {
        const val HORDE_SOCKET_PING_TIME_MILLIS = 10_000L
    }
}

class HordeStatusSource : HordeGenerationDataSource.StatusSource {
    private val processStatus = MutableSharedFlow<HordeProcessStatus>(
        extraBufferCapacity = 64,
    )
    private var _id: String? = null

    override var id: String?
        get() = _id
        set(value) {
            _id = value
        }

    override fun observe(): Flow<HordeProcessStatus> = processStatus

    override fun update(status: HordeProcessStatus) {
        processStatus.tryEmit(status)
    }
}
