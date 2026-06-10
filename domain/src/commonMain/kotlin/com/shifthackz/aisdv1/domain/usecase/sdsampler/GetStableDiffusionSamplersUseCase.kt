package com.shifthackz.aisdv1.domain.usecase.sdsampler

import com.shifthackz.aisdv1.domain.entity.StableDiffusionSampler

/**
 * Defines the `GetStableDiffusionSamplersUseCase` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
interface GetStableDiffusionSamplersUseCase {
    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @return Result produced by `invoke`.
     * @author Dmitriy Moroz
     */
    suspend operator fun invoke(): List<StableDiffusionSampler>
}
