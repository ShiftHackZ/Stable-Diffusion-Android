package com.shifthackz.aisdv1.domain.usecase.settings

import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials

/**
 * Defines the `ConnectToA1111UseCase` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
interface ConnectToA1111UseCase {
    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @param url remote URL used by the operation.
     * @param isDemo is demo value consumed by the API.
     * @param credentials credentials value consumed by the API.
     * @return Result produced by `invoke`.
     * @author Dmitriy Moroz
     */
    suspend operator fun invoke(
        url: String,
        isDemo: Boolean,
        credentials: AuthorizationCredentials,
    ): Result<Unit>
}
