package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.data.mappers.mapToAiGenResult
import com.shifthackz.aisdv1.data.mappers.mapToRequest
import com.shifthackz.aisdv1.data.provider.ServerUrlProvider
import com.shifthackz.aisdv1.domain.datasource.StableDiffusionGenerationDataSource
import com.shifthackz.aisdv1.domain.entity.ImageToImagePayload
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import com.shifthackz.aisdv1.network.api.automatic1111.Automatic1111RestApi
import com.shifthackz.aisdv1.network.api.automatic1111.Automatic1111RestApi.Companion.PATH_IMG_TO_IMG
import com.shifthackz.aisdv1.network.api.automatic1111.Automatic1111RestApi.Companion.PATH_TXT_TO_IMG
import com.shifthackz.aisdv1.network.extensions.withExceptionMapper
import com.shifthackz.aisdv1.network.response.SdGenerationResponse

internal class StableDiffusionGenerationRemoteDataSource(
    private val serverUrlProvider: ServerUrlProvider,
    private val api: Automatic1111RestApi,
) : StableDiffusionGenerationDataSource.Remote {

    override fun checkAvailability() = serverUrlProvider("/")
        .flatMapCompletable(api::healthCheck)

    override fun checkAvailability(url: String) = api.healthCheck(url)

    override fun textToImage(payload: TextToImagePayload) = serverUrlProvider(PATH_TXT_TO_IMG)
        .flatMap { url -> api.textToImage(url, payload.mapToRequest()) }
        .map { response -> payload to response }
        .map(Pair<TextToImagePayload, SdGenerationResponse>::mapToAiGenResult)
        .withExceptionMapper()

    override fun imageToImage(payload: ImageToImagePayload) = serverUrlProvider(PATH_IMG_TO_IMG)
        .flatMap { url -> api.imageToImage(url, payload.mapToRequest()) }
        .map { response -> payload to response }
        .map(Pair<ImageToImagePayload, SdGenerationResponse>::mapToAiGenResult)
        .withExceptionMapper()
}
