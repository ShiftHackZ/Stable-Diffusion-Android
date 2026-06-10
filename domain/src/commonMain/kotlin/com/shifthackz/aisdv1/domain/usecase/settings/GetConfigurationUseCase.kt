package com.shifthackz.aisdv1.domain.usecase.settings

import com.shifthackz.aisdv1.domain.entity.Configuration

/**
 * Defines the `GetConfigurationUseCase` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
interface GetConfigurationUseCase {
    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @return Result produced by `invoke`.
     * @author Dmitriy Moroz
     */
    suspend operator fun invoke(): Configuration
}
