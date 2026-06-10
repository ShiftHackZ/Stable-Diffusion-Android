package com.shifthackz.aisdv1.domain.usecase.connectivity

import com.shifthackz.aisdv1.domain.repository.StableDiffusionGenerationRepository

/**
 * Implements `PingStableDiffusionServiceUseCase` behavior in the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
internal class PingStableDiffusionServiceUseCaseImpl(
    /**
     * Exposes the `repository` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val repository: StableDiffusionGenerationRepository,
) : PingStableDiffusionServiceUseCase {

    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    override suspend operator fun invoke() = repository.checkApiAvailability()
}
