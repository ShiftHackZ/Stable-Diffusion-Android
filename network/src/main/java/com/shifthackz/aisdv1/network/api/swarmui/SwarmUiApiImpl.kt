package com.shifthackz.aisdv1.network.api.swarmui

import android.graphics.BitmapFactory
import com.shifthackz.aisdv1.network.request.SwarmUiGenerationRequest
import io.reactivex.rxjava3.core.Single

internal class SwarmUiApiImpl(
    private val rawApi: SwarmUiApi.RawApi,
) : SwarmUiApi {

    override fun getNewSession(url: String) = rawApi.getNewSession(url, emptyMap())

    override fun textToImage(
        url: String,
        request: SwarmUiGenerationRequest,
    ) = rawApi.textToImage(url, request)

    override fun downloadImage(url: String) = rawApi.download(url)
        .flatMap { response ->
            response.body()
                ?.bytes()
                ?.let { BitmapFactory.decodeByteArray(it, 0, it.size) }
                ?.let { Single.just(it) }
                ?: Single.error(Throwable("Body is null"))
        }
}
