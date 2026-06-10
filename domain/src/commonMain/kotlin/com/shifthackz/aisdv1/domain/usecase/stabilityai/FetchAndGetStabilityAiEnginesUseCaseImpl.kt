package com.shifthackz.aisdv1.domain.usecase.stabilityai

import com.shifthackz.aisdv1.domain.entity.StabilityAiEngine
import com.shifthackz.aisdv1.domain.preference.PreferenceManager

internal class FetchAndGetStabilityAiEnginesUseCaseImpl(
    private val fetchStabilityAiEnginesUseCase: FetchStabilityAiEnginesUseCase,
    private val preferenceManager: PreferenceManager,
) : FetchAndGetStabilityAiEnginesUseCase {

    override suspend fun invoke(): List<StabilityAiEngine> {
        val engines = fetchStabilityAiEnginesUseCase(preferenceManager.stabilityAiApiKey)
        if (!engines.map(StabilityAiEngine::id).contains(preferenceManager.stabilityAiEngineId)) {
            preferenceManager.stabilityAiEngineId = engines.firstOrNull()?.id ?: ""
        }
        return engines
    }
}
