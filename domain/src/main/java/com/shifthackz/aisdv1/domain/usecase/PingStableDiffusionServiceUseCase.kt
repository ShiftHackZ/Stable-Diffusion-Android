package com.shifthackz.aisdv1.domain.usecase

import io.reactivex.rxjava3.core.Completable

interface PingStableDiffusionServiceUseCase {
    fun execute(): Completable
}
