package com.shifthackz.aisdv1.network.api.huggingface

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.shifthackz.aisdv1.core.common.log.debugLog
import com.shifthackz.aisdv1.network.request.HuggingFaceGenerationRequest
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import java.util.concurrent.TimeUnit

internal class HuggingFaceInferenceApiImpl(
    private val rawApi: HuggingFaceInferenceApi.RawApi,
) : HuggingFaceInferenceApi {

    override fun generate(
        model: String,
        request: HuggingFaceGenerationRequest,
    ): Single<Bitmap> = rawApi
        .generate(model, request)
        .flatMapObservable { response ->
            if (response.isSuccessful) {
                response.body()
                    ?.bytes()
                    ?.let { BitmapFactory.decodeByteArray(it, 0, it.size) }
                    ?.let { Observable.just(it) }
                    ?: Observable.error(Throwable("Body is null"))
            } else {
                when (response.code()) {
                    503 -> Observable.error(RetryException())

                    else -> {
                        Observable.error(Throwable(response.errorBody()?.string().toString()))
                    }
                }
            }
        }
        .retryWhen { obs ->
            obs.flatMap { t ->
                if (t is RetryException) Observable
                    .timer(20L, TimeUnit.SECONDS)
                    .doOnNext { debugLog("Retrying hugging face due to 503...") }
                else
                    Observable.error(t)
            }
        }
        .let { Single.fromObservable(it) }

    private class RetryException : Throwable()
}
