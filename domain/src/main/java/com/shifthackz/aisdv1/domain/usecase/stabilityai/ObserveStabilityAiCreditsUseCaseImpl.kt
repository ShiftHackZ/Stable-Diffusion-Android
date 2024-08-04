package com.shifthackz.aisdv1.domain.usecase.stabilityai

import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.entity.Settings
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.repository.StabilityAiCreditsRepository
import io.reactivex.rxjava3.core.Flowable

internal class ObserveStabilityAiCreditsUseCaseImpl(
    private val repository: StabilityAiCreditsRepository,
    private val preferenceManager: PreferenceManager,
) : ObserveStabilityAiCreditsUseCase {

    override fun invoke(): Flowable<Float> = Flowable
        .combineLatest(
            preferenceManager.observe().map(Settings::source),
            repository.fetchAndObserve(),
            ::Pair,
        )
        .map(Pair<ServerSource, Float>::second)
        .onErrorReturn { 0f }
}
