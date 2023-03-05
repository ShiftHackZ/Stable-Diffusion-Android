package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.data.mappers.mapToAiGenResult
import com.shifthackz.aisdv1.data.mappers.mapToRequest
import com.shifthackz.aisdv1.domain.datasource.StableDiffusionTextToImageDataSource
import com.shifthackz.aisdv1.domain.entity.TextToImagePayloadDomain
import com.shifthackz.aisdv1.network.api.StableDiffusionWebUiAutomaticRestApi
import com.shifthackz.aisdv1.network.response.TextToImageResponse
import io.reactivex.rxjava3.core.Completable

class StableDiffusionTextToImageRemoteDataSource(
    private val api: StableDiffusionWebUiAutomaticRestApi,
) : StableDiffusionTextToImageDataSource.Remote {

    override fun checkAvailability(): Completable = api.healthCheck()

    override fun textToImage(payload: TextToImagePayloadDomain) = api
        .textToImage(payload.mapToRequest())
        .map { response -> payload to response }
        .map(Pair<TextToImagePayloadDomain, TextToImageResponse>::mapToAiGenResult)
}
