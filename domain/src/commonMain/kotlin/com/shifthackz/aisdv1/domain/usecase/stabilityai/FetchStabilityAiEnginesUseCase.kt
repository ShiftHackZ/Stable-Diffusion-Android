package com.shifthackz.aisdv1.domain.usecase.stabilityai

import com.shifthackz.aisdv1.domain.entity.StabilityAiEngine
import com.shifthackz.aisdv1.domain.repository.StabilityAiEnginesRepository

/**
 * Defines the `FetchStabilityAiEnginesUseCase` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
interface FetchStabilityAiEnginesUseCase {

    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @param apiKey api key value consumed by the API.
     * @return Result produced by `invoke`.
     * @author Dmitriy Moroz
     */
    suspend operator fun invoke(apiKey: String): List<StabilityAiEngine>
}

/**
 * Implements `FetchStabilityAiEnginesUseCase` behavior in the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
class FetchStabilityAiEnginesUseCaseImpl(
    /**
     * Exposes the `repository` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val repository: StabilityAiEnginesRepository,
) : FetchStabilityAiEnginesUseCase {

    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @param apiKey api key value consumed by the API.
     * @return Result produced by `invoke`.
     * @author Dmitriy Moroz
     */
    override suspend fun invoke(apiKey: String): List<StabilityAiEngine> =
        repository.fetch(apiKey)
}
