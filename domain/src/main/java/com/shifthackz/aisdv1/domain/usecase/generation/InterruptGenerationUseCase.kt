package com.shifthackz.aisdv1.domain.usecase.generation

import io.reactivex.rxjava3.core.Completable

interface InterruptGenerationUseCase {
    operator fun invoke(): Completable
}
