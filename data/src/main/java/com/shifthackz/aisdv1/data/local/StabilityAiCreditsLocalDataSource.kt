package com.shifthackz.aisdv1.data.local

import com.shifthackz.aisdv1.domain.datasource.StabilityAiCreditsDataSource
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.subjects.BehaviorSubject

internal class StabilityAiCreditsLocalDataSource(
    private val creditsSubject: BehaviorSubject<Float> = BehaviorSubject.createDefault(0f),
) : StabilityAiCreditsDataSource.Local {

    override fun get() = creditsSubject
        .firstOrError()
        .onErrorReturn { 0f }

    override fun save(value: Float) = Completable.fromAction {
        creditsSubject.onNext(value)
    }

    override fun observe() = creditsSubject.toFlowable(BackpressureStrategy.LATEST)
}
