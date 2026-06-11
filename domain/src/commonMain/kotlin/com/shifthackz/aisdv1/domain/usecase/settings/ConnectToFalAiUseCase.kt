package com.shifthackz.aisdv1.domain.usecase.settings

/**
 * Defines the `ConnectToFalAiUseCase` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
interface ConnectToFalAiUseCase {
    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @param apiKey api key value consumed by the API.
     * @return Result produced by `invoke`.
     * @author Dmitriy Moroz
     */
    suspend operator fun invoke(apiKey: String): Result<Unit>
}
