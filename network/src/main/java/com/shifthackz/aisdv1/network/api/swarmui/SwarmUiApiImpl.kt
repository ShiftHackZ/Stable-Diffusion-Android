package com.shifthackz.aisdv1.network.api.swarmui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.shifthackz.aisdv1.network.exception.BadSessionException
import com.shifthackz.aisdv1.network.request.SwarmUiGenerationRequest
import com.shifthackz.aisdv1.network.request.SwarmUiModelsRequest
import com.shifthackz.aisdv1.network.response.SwarmUiGenerationResponse
import com.shifthackz.aisdv1.network.response.SwarmUiModelsResponse
import com.shifthackz.aisdv1.network.response.SwarmUiSessionResponse
import io.reactivex.rxjava3.core.Single
import retrofit2.HttpException

internal class SwarmUiApiImpl(
    private val rawApi: SwarmUiApi.RawApi,
) : SwarmUiApi {

    override fun getNewSession(url: String): Single<SwarmUiSessionResponse> = rawApi
        .getNewSession(url, emptyMap())
        .mapError()

    override fun generate(
        url: String,
        request: SwarmUiGenerationRequest,
    ): Single<SwarmUiGenerationResponse> = rawApi
        .generate(url, request)
        .mapError()

    override fun fetchModels(
        url: String,
        request: SwarmUiModelsRequest
    ): Single<SwarmUiModelsResponse> = rawApi
        .fetchModels(url, request)
        .mapError()

    override fun downloadImage(url: String): Single<Bitmap> = rawApi
        .download(url)
        .mapError()
        .flatMap { response ->
            response.body()
                ?.bytes()
                ?.let { BitmapFactory.decodeByteArray(it, 0, it.size) }
                ?.let { Single.just(it) }
                ?: Single.error(Throwable("Body is null"))
        }

    private fun <T : Any> Single<T>.mapError(): Single<T> = this.onErrorResumeNext { t ->
        if (t is HttpException && t.code() == 401) {
            Single.error(BadSessionException())
        } else {
            Single.error(t)
        }
    }
}
