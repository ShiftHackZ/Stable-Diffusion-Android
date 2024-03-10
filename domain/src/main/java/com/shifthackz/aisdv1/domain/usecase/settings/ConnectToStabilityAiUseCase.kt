package com.shifthackz.aisdv1.domain.usecase.settings

import io.reactivex.rxjava3.core.Single

interface ConnectToStabilityAiUseCase {
    operator fun invoke(apiKey: String): Single<Result<Unit>>
}
