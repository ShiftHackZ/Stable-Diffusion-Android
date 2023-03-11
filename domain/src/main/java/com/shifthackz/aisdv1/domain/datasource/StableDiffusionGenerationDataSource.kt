package com.shifthackz.aisdv1.domain.datasource

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.ImageToImagePayload
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

sealed interface StableDiffusionGenerationDataSource {
    interface Remote : StableDiffusionGenerationDataSource {
        fun checkAvailability(): Completable
        fun checkAvailability(url: String): Completable
        fun textToImage(payload: TextToImagePayload): Single<AiGenerationResult>
        fun imageToImage(payload: ImageToImagePayload): Single<AiGenerationResult>
    }
}
