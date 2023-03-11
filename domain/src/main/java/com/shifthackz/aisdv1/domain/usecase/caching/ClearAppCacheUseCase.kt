package com.shifthackz.aisdv1.domain.usecase.caching

import io.reactivex.rxjava3.core.Completable

interface ClearAppCacheUseCase {
    operator fun invoke(): Completable
}
