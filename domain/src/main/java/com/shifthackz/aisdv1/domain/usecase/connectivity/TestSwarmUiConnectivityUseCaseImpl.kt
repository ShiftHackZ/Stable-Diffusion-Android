package com.shifthackz.aisdv1.domain.usecase.connectivity

import com.shifthackz.aisdv1.domain.repository.SwarmUiGenerationRepository

class TestSwarmUiConnectivityUseCaseImpl(
    private val repository: SwarmUiGenerationRepository,
) : TestSwarmUiConnectivityUseCase {

    override fun invoke(url: String) = repository.checkApiAvailability(url)
}
