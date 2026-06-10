package com.shifthackz.aisdv1.domain.usecase.connectivity

/**
 * Defines the `TestSwarmUiConnectivityUseCase` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
interface TestSwarmUiConnectivityUseCase {
    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @param url remote URL used by the operation.
     * @author Dmitriy Moroz
     */
    suspend operator fun invoke(url: String)
}
