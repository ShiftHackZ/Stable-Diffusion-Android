package com.shifthackz.aisdv1.network.api.swarmui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.shifthackz.aisdv1.network.request.SwarmUiGenerationRequest
import com.shifthackz.aisdv1.network.request.SwarmUiModelsRequest
import com.shifthackz.aisdv1.network.response.SwarmUiGenerationResponse
import com.shifthackz.aisdv1.network.response.SwarmUiModelsResponse
import com.shifthackz.aisdv1.network.response.SwarmUiSessionResponse
import io.reactivex.rxjava3.core.Single

internal class SwarmUiApiImpl(
    private val rawApi: SwarmUiApi.RawApi,
) : SwarmUiApi {

    override fun getNewSession(url: String): Single<SwarmUiSessionResponse> = rawApi
        .getNewSession(url, emptyMap())

    override fun generate(
        url: String,
        request: SwarmUiGenerationRequest,
    ): Single<SwarmUiGenerationResponse> = rawApi.generate(url, request)

    override fun fetchModels(
        url: String,
        request: SwarmUiModelsRequest
    ): Single<SwarmUiModelsResponse> = rawApi.fetchModels(url, request)

    override fun downloadImage(url: String): Single<Bitmap> = rawApi
        .download(url)
        .flatMap { response ->
            response.body()
                ?.bytes()
                ?.let { BitmapFactory.decodeByteArray(it, 0, it.size) }
                ?.let { Single.just(it) }
                ?: Single.error(Throwable("Body is null"))
        }
}
