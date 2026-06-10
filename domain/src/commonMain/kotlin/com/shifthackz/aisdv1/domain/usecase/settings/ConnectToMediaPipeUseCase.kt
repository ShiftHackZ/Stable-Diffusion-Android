package com.shifthackz.aisdv1.domain.usecase.settings

/**
 * Defines the `ConnectToMediaPipeUseCase` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
interface ConnectToMediaPipeUseCase {
    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @param modelId model id value consumed by the API.
     * @param modelPath model path value consumed by the API.
     * @return Result produced by `invoke`.
     * @author Dmitriy Moroz
     */
    suspend operator fun invoke(
        modelId: String,
        modelPath: String,
    ): Result<Unit>
}
