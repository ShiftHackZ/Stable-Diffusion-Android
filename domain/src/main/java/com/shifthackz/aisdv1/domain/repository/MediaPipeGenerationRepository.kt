package com.shifthackz.aisdv1.domain.repository

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import io.reactivex.rxjava3.core.Single

interface MediaPipeGenerationRepository {
    fun generateFromText(payload: TextToImagePayload): Single<AiGenerationResult>
}
