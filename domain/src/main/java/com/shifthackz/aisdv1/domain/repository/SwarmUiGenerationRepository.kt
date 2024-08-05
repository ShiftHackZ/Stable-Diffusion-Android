package com.shifthackz.aisdv1.domain.repository

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.ImageToImagePayload
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

interface SwarmUiGenerationRepository {
    fun checkApiAvailability(): Completable
    fun checkApiAvailability(url: String): Completable
    fun generateFromText(payload: TextToImagePayload): Single<AiGenerationResult>
    fun generateFromImage(payload: ImageToImagePayload): Single<AiGenerationResult>
}
