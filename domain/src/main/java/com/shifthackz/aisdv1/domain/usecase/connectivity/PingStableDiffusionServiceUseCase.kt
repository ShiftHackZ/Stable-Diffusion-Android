package com.shifthackz.aisdv1.domain.usecase.connectivity

import io.reactivex.rxjava3.core.Completable

interface PingStableDiffusionServiceUseCase {
    operator fun invoke(): Completable
}
