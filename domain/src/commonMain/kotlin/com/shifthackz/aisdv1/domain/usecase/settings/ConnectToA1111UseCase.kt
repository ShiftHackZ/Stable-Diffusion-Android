package com.shifthackz.aisdv1.domain.usecase.settings

import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials

interface ConnectToA1111UseCase {
    suspend operator fun invoke(
        url: String,
        isDemo: Boolean,
        credentials: AuthorizationCredentials,
    ): Result<Unit>
}
