package com.shifthackz.aisdv1.domain.usecase.connectivity

import com.shifthackz.aisdv1.domain.repository.StableDiffusionGenerationRepository

internal class TestConnectivityUseCaseImpl(
    private val repository: StableDiffusionGenerationRepository,
) : TestConnectivityUseCase {

    override fun invoke(url: String) = repository.checkApiAvailability(url)
}
