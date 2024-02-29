package com.shifthackz.aisdv1.domain.usecase.settings

import io.reactivex.rxjava3.core.Single

interface ConnectToHuggingFaceUseCase {
    operator fun invoke(apiKey: String, model: String): Single<Result<Unit>>
}
