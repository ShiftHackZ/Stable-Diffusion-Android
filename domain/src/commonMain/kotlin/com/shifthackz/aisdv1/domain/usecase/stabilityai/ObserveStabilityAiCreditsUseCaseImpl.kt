package com.shifthackz.aisdv1.domain.usecase.stabilityai

import com.shifthackz.aisdv1.domain.repository.StabilityAiCreditsRepository
import kotlinx.coroutines.flow.catch

class ObserveStabilityAiCreditsUseCaseImpl(
    private val repository: StabilityAiCreditsRepository,
) : ObserveStabilityAiCreditsUseCase {

    override fun invoke() = repository
        .fetchAndObserve()
        .catch { emit(0f) }
}
