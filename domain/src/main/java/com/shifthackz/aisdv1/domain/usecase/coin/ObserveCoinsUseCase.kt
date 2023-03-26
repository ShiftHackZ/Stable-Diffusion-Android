package com.shifthackz.aisdv1.domain.usecase.coin

import io.reactivex.rxjava3.core.Flowable

interface ObserveCoinsUseCase {
    operator fun invoke(): Flowable<Result>

    sealed interface Result {
        object FeatureNotAvailable : Result
        object UsingOwnServer : Result
        data class Coins(val value: Int) : Result
    }
}
