package com.shifthackz.aisdv1.domain.usecase.downloadable

import io.reactivex.rxjava3.core.Completable

interface DeleteModelUseCase {
    operator fun invoke(): Completable
}
