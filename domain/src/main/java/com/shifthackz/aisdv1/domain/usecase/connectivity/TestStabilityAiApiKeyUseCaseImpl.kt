package com.shifthackz.aisdv1.domain.usecase.connectivity

import com.shifthackz.aisdv1.domain.repository.StabilityAiGenerationRepository

internal class TestStabilityAiApiKeyUseCaseImpl(
    private val stabilityAiGenerationRepository: StabilityAiGenerationRepository,
) : TestStabilityAiApiKeyUseCase {

    override fun invoke() = stabilityAiGenerationRepository.validateApiKey()
}
