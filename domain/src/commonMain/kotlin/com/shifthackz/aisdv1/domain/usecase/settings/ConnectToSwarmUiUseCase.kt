package com.shifthackz.aisdv1.domain.usecase.settings

import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials

/**
 * Defines the `ConnectToSwarmUiUseCase` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
interface ConnectToSwarmUiUseCase {
    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @param url remote URL used by the operation.
     * @param credentials credentials value consumed by the API.
     * @return Result produced by `invoke`.
     * @author Dmitriy Moroz
     */
    suspend operator fun invoke(
        url: String,
        credentials: AuthorizationCredentials,
    ): Result<Unit>
}
