package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.data.mappers.mapToRequest
import com.shifthackz.aisdv1.domain.datasource.StableDiffusionTextToImageDataSource
import com.shifthackz.aisdv1.domain.entity.AiGenerationResultDomain
import com.shifthackz.aisdv1.domain.entity.TextToImagePayloadDomain
import com.shifthackz.aisdv1.network.api.StableDiffusionWebUiAutomaticRestApi
import com.shifthackz.aisdv1.network.request.TextToImageRequest
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

class StableDiffusionTextToImageRemoteDataSource(
    private val api: StableDiffusionWebUiAutomaticRestApi,
) : StableDiffusionTextToImageDataSource.Remote {

    override fun checkAvailability(): Completable = api.healthCheck()

    override fun textToImage(payload: TextToImagePayloadDomain): Single<AiGenerationResultDomain> = api
        .textToImage(payload.mapToRequest())
        .map { AiGenerationResultDomain(it.images.firstOrNull() ?: "") }
}
