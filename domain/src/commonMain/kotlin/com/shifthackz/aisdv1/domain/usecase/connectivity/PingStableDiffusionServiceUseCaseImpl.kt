package com.shifthackz.aisdv1.domain.usecase.connectivity

import com.shifthackz.aisdv1.domain.repository.StableDiffusionGenerationRepository

internal class PingStableDiffusionServiceUseCaseImpl(
    private val repository: StableDiffusionGenerationRepository,
) : PingStableDiffusionServiceUseCase {

    override suspend operator fun invoke() = repository.checkApiAvailability()
}
