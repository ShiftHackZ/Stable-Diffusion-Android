package com.shifthackz.aisdv1.domain.datasource

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

sealed interface StableDiffusionTextToImageDataSource {
    interface Remote : StableDiffusionTextToImageDataSource {
        fun checkAvailability(): Completable
        fun textToImage(payload: TextToImagePayload): Single<AiGenerationResult>
    }
}
