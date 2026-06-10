package com.shifthackz.aisdv1.domain.usecase.sdlora

import com.shifthackz.aisdv1.domain.entity.LoRA

/**
 * Defines the `FetchAndGetLorasUseCase` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
interface FetchAndGetLorasUseCase {
    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @return Result produced by `invoke`.
     * @author Dmitriy Moroz
     */
    suspend operator fun invoke(): List<LoRA>
}
