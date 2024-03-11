package com.shifthackz.aisdv1.domain.repository

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single

interface StabilityAiCreditsRepository {
    fun fetch(): Completable
    fun fetchAndGet(): Single<Float>
    fun fetchAndObserve(): Flowable<Float>
    fun get(): Single<Float>
    fun observe(): Flowable<Float>
}
