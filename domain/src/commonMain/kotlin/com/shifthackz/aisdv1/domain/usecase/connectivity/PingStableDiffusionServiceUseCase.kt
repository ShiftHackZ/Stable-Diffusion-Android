package com.shifthackz.aisdv1.domain.usecase.connectivity

/**
 * Defines the `PingStableDiffusionServiceUseCase` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
interface PingStableDiffusionServiceUseCase {
    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    suspend operator fun invoke()
}
