package com.shifthackz.aisdv1.domain.repository

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

interface StableDiffusionTextToImageRepository {
    fun checkApiAvailability(): Completable
    fun generateAndGetImage(payload: TextToImagePayload): Single<AiGenerationResult>
}
