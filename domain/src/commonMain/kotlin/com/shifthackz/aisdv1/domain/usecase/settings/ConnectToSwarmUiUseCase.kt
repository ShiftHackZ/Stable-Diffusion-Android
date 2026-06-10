package com.shifthackz.aisdv1.domain.usecase.settings

import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials

interface ConnectToSwarmUiUseCase {
    suspend operator fun invoke(
        url: String,
        credentials: AuthorizationCredentials,
    ): Result<Unit>
}
