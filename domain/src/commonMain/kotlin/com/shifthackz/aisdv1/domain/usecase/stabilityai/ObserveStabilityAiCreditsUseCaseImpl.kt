package com.shifthackz.aisdv1.domain.usecase.stabilityai

import com.shifthackz.aisdv1.domain.repository.StabilityAiCreditsRepository
import kotlinx.coroutines.flow.catch

/**
 * Implements `ObserveStabilityAiCreditsUseCase` behavior in the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
class ObserveStabilityAiCreditsUseCaseImpl(
    /**
     * Exposes the `repository` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val repository: StabilityAiCreditsRepository,
) : ObserveStabilityAiCreditsUseCase {

    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    override fun invoke() = repository
        .fetchAndObserve()
        .catch { emit(0f) }
}
