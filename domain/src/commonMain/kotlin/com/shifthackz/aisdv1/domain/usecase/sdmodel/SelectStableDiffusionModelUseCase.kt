package com.shifthackz.aisdv1.domain.usecase.sdmodel

/**
 * Defines the `SelectStableDiffusionModelUseCase` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
interface SelectStableDiffusionModelUseCase {
    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @param modelName model name value consumed by the API.
     * @author Dmitriy Moroz
     */
    suspend operator fun invoke(modelName: String)
}
