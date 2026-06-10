package com.shifthackz.aisdv1.domain.usecase.sdmodel

import com.shifthackz.aisdv1.domain.entity.StableDiffusionModel

/**
 * Defines the `GetStableDiffusionModelsUseCase` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
interface GetStableDiffusionModelsUseCase {
    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @return Result produced by `invoke`.
     * @author Dmitriy Moroz
     */
    suspend operator fun invoke(): List<Pair<StableDiffusionModel, Boolean>>
}
