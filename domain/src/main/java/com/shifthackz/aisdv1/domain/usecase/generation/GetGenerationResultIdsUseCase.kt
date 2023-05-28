package com.shifthackz.aisdv1.domain.usecase.generation

import io.reactivex.rxjava3.core.Single

interface GetGenerationResultIdsUseCase {
    operator fun invoke(): Single<List<Long>>
}
