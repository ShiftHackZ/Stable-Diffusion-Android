package com.shifthackz.aisdv1.domain.usecase.connectivity

import com.shifthackz.aisdv1.domain.repository.HordeGenerationRepository

internal class TestHordeApiKeyUseCaseImpl(
    private val hordeGenerationRepository: HordeGenerationRepository,
) : TestHordeApiKeyUseCase {

    override fun invoke() = hordeGenerationRepository.validateApiKey()
}
