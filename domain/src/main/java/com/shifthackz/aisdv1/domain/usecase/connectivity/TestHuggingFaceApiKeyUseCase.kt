package com.shifthackz.aisdv1.domain.usecase.connectivity

import io.reactivex.rxjava3.core.Single

interface TestHuggingFaceApiKeyUseCase {
    operator fun invoke(): Single<Boolean>
}
