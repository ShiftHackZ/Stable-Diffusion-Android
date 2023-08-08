package com.shifthackz.aisdv1.domain.repository

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import com.shifthackz.aisdv1.domain.feature.diffusion.LocalDiffusion
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

interface LocalDiffusionGenerationRepository {
    fun observeStatus(): Observable<LocalDiffusion.Status>
    fun generateFromText(payload: TextToImagePayload): Single<AiGenerationResult>
}
