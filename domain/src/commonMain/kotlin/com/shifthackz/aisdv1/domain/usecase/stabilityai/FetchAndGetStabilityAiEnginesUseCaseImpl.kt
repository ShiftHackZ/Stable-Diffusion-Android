package com.shifthackz.aisdv1.domain.usecase.stabilityai

import com.shifthackz.aisdv1.domain.entity.StabilityAiEngine
import com.shifthackz.aisdv1.domain.preference.PreferenceManager

/**
 * Implements `FetchAndGetStabilityAiEnginesUseCase` behavior in the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
internal class FetchAndGetStabilityAiEnginesUseCaseImpl(
    /**
     * Exposes the `fetchStabilityAiEnginesUseCase` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val fetchStabilityAiEnginesUseCase: FetchStabilityAiEnginesUseCase,
    /**
     * Exposes the `preferenceManager` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val preferenceManager: PreferenceManager,
) : FetchAndGetStabilityAiEnginesUseCase {

    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @return Result produced by `invoke`.
     * @author Dmitriy Moroz
     */
    override suspend fun invoke(): List<StabilityAiEngine> {
        val engines = fetchStabilityAiEnginesUseCase(preferenceManager.stabilityAiApiKey)
        if (!engines.map(StabilityAiEngine::id).contains(preferenceManager.stabilityAiEngineId)) {
            preferenceManager.stabilityAiEngineId = engines.firstOrNull()?.id ?: ""
        }
        return engines
    }
}
