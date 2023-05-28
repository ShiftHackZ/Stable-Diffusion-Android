package com.shifthackz.aisdv1.domain.usecase.generation

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import io.reactivex.rxjava3.core.Single

interface GetGenerationResultPagedUseCase {
    operator fun invoke(limit: Int, offset: Int): Single<List<AiGenerationResult>>
}
