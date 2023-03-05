package com.shifthackz.aisdv1.domain.repository

import com.shifthackz.aisdv1.domain.entity.AiGenerationResultDomain
import com.shifthackz.aisdv1.domain.entity.TextToImagePayloadDomain
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

interface StableDiffusionTextToImageRepository {
    fun checkApiAvailability(): Completable
    fun generateAndGetImage(payload: TextToImagePayloadDomain): Single<AiGenerationResultDomain>
}
