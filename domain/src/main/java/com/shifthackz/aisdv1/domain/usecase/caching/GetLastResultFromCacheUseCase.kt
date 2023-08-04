package com.shifthackz.aisdv1.domain.usecase.caching

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import io.reactivex.rxjava3.core.Single

interface GetLastResultFromCacheUseCase {
    operator fun invoke(): Single<AiGenerationResult>
}
