package com.shifthackz.aisdv1.domain.usecase.settings

import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials
import io.reactivex.rxjava3.core.Single

interface ConnectToA1111UseCase {
    operator fun invoke(
        url: String,
        isDemo: Boolean,
        credentials: AuthorizationCredentials,
    ): Single<Result<Unit>>
}
