package com.shifthackz.aisdv1.domain.usecase.stabilityai

import com.shifthackz.aisdv1.domain.entity.StabilityAiEngine
import com.shifthackz.aisdv1.domain.repository.StabilityAiEnginesRepository

interface FetchStabilityAiEnginesUseCase {

    suspend operator fun invoke(apiKey: String): List<StabilityAiEngine>
}

class FetchStabilityAiEnginesUseCaseImpl(
    private val repository: StabilityAiEnginesRepository,
) : FetchStabilityAiEnginesUseCase {

    override suspend fun invoke(apiKey: String): List<StabilityAiEngine> =
        repository.fetch(apiKey)
}
