package com.shifthackz.aisdv1.domain.usecase.connectivity

import com.shifthackz.aisdv1.domain.repository.StabilityAiGenerationRepository
import io.reactivex.rxjava3.core.Single

internal class TestStabilityAiApiKeyUseCaseImpl(
    private val stabilityAiGenerationRepository: StabilityAiGenerationRepository,
) : TestStabilityAiApiKeyUseCase {

    override fun invoke(): Single<Boolean> = stabilityAiGenerationRepository.validateApiKey()
}
