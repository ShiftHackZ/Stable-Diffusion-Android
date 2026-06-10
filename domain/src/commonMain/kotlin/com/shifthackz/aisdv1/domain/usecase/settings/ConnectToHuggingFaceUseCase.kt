package com.shifthackz.aisdv1.domain.usecase.settings

/**
 * Defines the `ConnectToHuggingFaceUseCase` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
interface ConnectToHuggingFaceUseCase {
    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @param apiKey api key value consumed by the API.
     * @param model model value consumed by the API.
     * @return Result produced by `invoke`.
     * @author Dmitriy Moroz
     */
    suspend operator fun invoke(apiKey: String, model: String): Result<Unit>
}
