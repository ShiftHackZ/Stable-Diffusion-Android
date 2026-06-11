package com.shifthackz.aisdv1.domain.usecase.forgemodule

import com.shifthackz.aisdv1.domain.entity.ForgeModule

/**
 * Defines the `GetForgeModulesUseCase` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
interface GetForgeModulesUseCase {

    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @return Result produced by `invoke`.
     * @author Dmitriy Moroz
     */
    suspend operator fun invoke(): List<ForgeModule>
}
