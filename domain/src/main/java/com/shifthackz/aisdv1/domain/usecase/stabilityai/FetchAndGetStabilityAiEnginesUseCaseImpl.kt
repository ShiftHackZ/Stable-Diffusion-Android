package com.shifthackz.aisdv1.domain.usecase.stabilityai

import com.shifthackz.aisdv1.domain.entity.StabilityAiEngine
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.repository.StabilityAiEnginesRepository
import io.reactivex.rxjava3.core.Single

internal class FetchAndGetStabilityAiEnginesUseCaseImpl(
    private val repository: StabilityAiEnginesRepository,
    private val preferenceManager: PreferenceManager,
) : FetchAndGetStabilityAiEnginesUseCase {

    override fun invoke() = repository
        .fetchAndGet()
        .flatMap { engines ->
            if (!engines.map(StabilityAiEngine::id).contains(preferenceManager.stabilityAiEngineId)) {
                preferenceManager.stabilityAiEngineId = engines.firstOrNull()?.id ?: ""
            }
            Single.just(engines)
        }
}
