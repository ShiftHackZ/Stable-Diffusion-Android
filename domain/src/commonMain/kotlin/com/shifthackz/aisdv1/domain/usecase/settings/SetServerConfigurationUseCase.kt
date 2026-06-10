package com.shifthackz.aisdv1.domain.usecase.settings

import com.shifthackz.aisdv1.domain.entity.Configuration

/**
 * Defines the `SetServerConfigurationUseCase` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
interface SetServerConfigurationUseCase {
    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @param configuration configuration value consumed by the API.
     * @author Dmitriy Moroz
     */
    suspend operator fun invoke(configuration: Configuration)
}
