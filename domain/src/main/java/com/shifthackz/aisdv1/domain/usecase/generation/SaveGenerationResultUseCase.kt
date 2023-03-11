package com.shifthackz.aisdv1.domain.usecase.generation

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import io.reactivex.rxjava3.core.Completable

interface SaveGenerationResultUseCase {
    operator fun invoke(result: AiGenerationResult): Completable
}
