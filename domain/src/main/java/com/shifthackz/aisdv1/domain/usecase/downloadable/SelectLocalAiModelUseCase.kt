package com.shifthackz.aisdv1.domain.usecase.downloadable

import io.reactivex.rxjava3.core.Completable

interface SelectLocalAiModelUseCase {
    operator fun invoke(id: String): Completable
}
