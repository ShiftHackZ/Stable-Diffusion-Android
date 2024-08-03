package com.shifthackz.aisdv1.data.remote

import android.graphics.BitmapFactory
import com.shifthackz.aisdv1.core.common.log.debugLog
import com.shifthackz.aisdv1.core.imageprocessing.BitmapToBase64Converter
import com.shifthackz.aisdv1.data.mappers.mapCloudToAiGenResult
import com.shifthackz.aisdv1.data.mappers.mapToHordeRequest
import com.shifthackz.aisdv1.domain.datasource.HordeGenerationDataSource
import com.shifthackz.aisdv1.domain.entity.HordeProcessStatus
import com.shifthackz.aisdv1.domain.entity.ImageToImagePayload
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import com.shifthackz.aisdv1.network.api.horde.HordeRestApi
import com.shifthackz.aisdv1.network.request.HordeGenerationAsyncRequest
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.subjects.PublishSubject
import java.net.URL
import java.util.concurrent.TimeUnit

internal class HordeGenerationRemoteDataSource(
    private val hordeApi: HordeRestApi,
    private val converter: BitmapToBase64Converter,
    private val statusSource: HordeGenerationDataSource.StatusSource,
) : HordeGenerationDataSource.Remote {

    override fun validateApiKey() = hordeApi
        .checkHordeApiKey()
        .map { user -> user.id != null }
        .onErrorReturn { false }

    override fun textToImage(payload: TextToImagePayload) = Single
        .just(payload.mapToHordeRequest())
        .flatMap(::executeRequestChain)
        .map { base64 -> payload to base64 }
        .map(Pair<TextToImagePayload, String>::mapCloudToAiGenResult)

    override fun imageToImage(payload: ImageToImagePayload) = Single
        .just(payload.mapToHordeRequest())
        .flatMap(::executeRequestChain)
        .map { base64 -> payload to base64 }
        .map(Pair<ImageToImagePayload, String>::mapCloudToAiGenResult)

    override fun interruptGeneration() = statusSource.id
        ?.let(hordeApi::cancelRequest)
        ?: Completable.error(IllegalStateException("No cached request id"))

    private fun executeRequestChain(request: HordeGenerationAsyncRequest) = hordeApi
        .generateAsync(request)
        .flatMapObservable { asyncStartResponse ->
            statusSource.id = asyncStartResponse.id
            asyncStartResponse.id?.let { id ->
                Observable
                    .fromSingle(hordeApi.checkGeneration(id))
                    .flatMap { pingResponse ->
                        if (pingResponse.isPossible == false) {
                            return@flatMap Observable.error(Throwable("Response is not possible"))
                        }
                        if (pingResponse.done == true) {
                            return@flatMap Observable.fromSingle(hordeApi.checkStatus(id))
                        }
                        statusSource.update(
                            HordeProcessStatus(
                                waitTimeSeconds = pingResponse.waitTime ?: 0,
                                queuePosition = pingResponse.queuePosition,
                            )
                        )
                        return@flatMap Observable.error(RetryException())
                    }
                    .retryWhen { obs ->
                        obs.flatMap { t ->
                            if (t is RetryException) Observable
                                .timer(HORDE_SOCKET_PING_TIME_SECONDS, TimeUnit.SECONDS)
                                .doOnNext {
                                    debugLog("Retrying HORDE status check...")
                                }
                            else
                                Observable.error(t)
                        }
                    }
            } ?: Observable.error(Throwable("Horde returned null generation id"))
        }
        .flatMapSingle {
            it.generations?.firstOrNull()?.let { generation ->
                val bytes = URL(generation.img).readBytes()
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                Single.just(bitmap)
            } ?: Single.error(Throwable("Error extracting image"))
        }
        .flatMapSingle { converter(BitmapToBase64Converter.Input(it)) }
        .map { it.base64ImageString }
        .let { Single.fromObservable(it) }


    private class RetryException : Throwable()

    companion object {
        private const val HORDE_SOCKET_PING_TIME_SECONDS = 10L
    }
}

internal class HordeStatusSource : HordeGenerationDataSource.StatusSource {
    private val processStatusSubject: PublishSubject<HordeProcessStatus> = PublishSubject.create()
    private var _id: String? = null

    override var id: String?
        get() = _id
        set(value) { _id = value }

    override fun observe(): Flowable<HordeProcessStatus> = processStatusSubject
        .toFlowable(BackpressureStrategy.LATEST)

    override fun update(status: HordeProcessStatus) {
        processStatusSubject.onNext(status)
    }
}
