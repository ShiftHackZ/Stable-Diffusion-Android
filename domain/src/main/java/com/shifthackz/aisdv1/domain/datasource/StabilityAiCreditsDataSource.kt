package com.shifthackz.aisdv1.domain.datasource

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single

sealed interface StabilityAiCreditsDataSource {

    interface Remote : StabilityAiCreditsDataSource {
        fun fetch(): Single<Float>
    }

    interface Local : StabilityAiCreditsDataSource {
        fun get(): Single<Float>
        fun save(value: Float): Completable
        fun observe(): Flowable<Float>
    }
}
