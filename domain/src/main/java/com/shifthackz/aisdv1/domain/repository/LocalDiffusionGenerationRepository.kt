package com.shifthackz.aisdv1.domain.repository

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.LocalDiffusionStatus
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

interface LocalDiffusionGenerationRepository {
    fun observeStatus(): Observable<LocalDiffusionStatus>
    fun generateFromText(payload: TextToImagePayload): Single<AiGenerationResult>
    fun interruptGeneration(): Completable
}
