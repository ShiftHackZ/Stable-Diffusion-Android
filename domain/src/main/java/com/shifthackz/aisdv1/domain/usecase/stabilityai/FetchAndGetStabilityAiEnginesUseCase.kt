package com.shifthackz.aisdv1.domain.usecase.stabilityai

import com.shifthackz.aisdv1.domain.entity.StabilityAiEngine
import io.reactivex.rxjava3.core.Single

interface FetchAndGetStabilityAiEnginesUseCase {
    operator fun invoke(): Single<List<StabilityAiEngine>>
}
