package com.shifthackz.aisdv1.domain.usecase.stabilityai

import io.reactivex.rxjava3.core.Flowable

interface ObserveStabilityAiCreditsUseCase {
    operator fun invoke(): Flowable<Float>
}
