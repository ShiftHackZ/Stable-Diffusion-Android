package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.core.common.extensions.fixUrlSlashes
import com.shifthackz.aisdv1.data.mappers.mapToAiGenResult
import com.shifthackz.aisdv1.data.mappers.mapToRequest
import com.shifthackz.aisdv1.data.provider.ServerUrlProvider
import com.shifthackz.aisdv1.domain.datasource.StableDiffusionGenerationDataSource
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.ImageToImagePayload
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import com.shifthackz.aisdv1.network.api.automatic1111.Automatic1111RestApi
import com.shifthackz.aisdv1.network.api.automatic1111.Automatic1111RestApi.Companion.PATH_IMG_TO_IMG
import com.shifthackz.aisdv1.network.api.automatic1111.Automatic1111RestApi.Companion.PATH_TXT_TO_IMG
import com.shifthackz.aisdv1.network.response.SdGenerationResponse
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

internal class StableDiffusionGenerationRemoteDataSource(
    private val serverUrlProvider: ServerUrlProvider,
    private val api: Automatic1111RestApi,
) : StableDiffusionGenerationDataSource.Remote {

    override fun checkAvailability(): Completable = serverUrlProvider("/")
        .flatMapCompletable(api::healthCheck)

    override fun checkAvailability(url: String): Completable = api.healthCheck(url.fixUrlSlashes())

    override fun textToImage(payload: TextToImagePayload): Single<AiGenerationResult> = serverUrlProvider(PATH_TXT_TO_IMG)
        .flatMap { url -> api.textToImage(url, payload.mapToRequest()) }
        .map { response -> payload to response }
        .map(Pair<TextToImagePayload, SdGenerationResponse>::mapToAiGenResult)

    override fun imageToImage(payload: ImageToImagePayload): Single<AiGenerationResult> = serverUrlProvider(PATH_IMG_TO_IMG)
        .flatMap { url -> api.imageToImage(url, payload.mapToRequest()) }
        .map { response -> payload to response }
        .map(Pair<ImageToImagePayload, SdGenerationResponse>::mapToAiGenResult)

    override fun interruptGeneration(): Completable = serverUrlProvider(Automatic1111RestApi.PATH_INTERRUPT)
        .flatMapCompletable(api::interrupt)
}
