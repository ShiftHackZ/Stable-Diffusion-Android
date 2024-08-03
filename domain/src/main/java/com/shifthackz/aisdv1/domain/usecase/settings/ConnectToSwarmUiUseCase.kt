package com.shifthackz.aisdv1.domain.usecase.settings

import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials
import io.reactivex.rxjava3.core.Single

interface ConnectToSwarmUiUseCase {
    operator fun invoke(url: String, credentials: AuthorizationCredentials): Single<Result<Unit>>
}
