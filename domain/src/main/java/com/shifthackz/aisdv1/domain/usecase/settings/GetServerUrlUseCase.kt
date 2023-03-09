package com.shifthackz.aisdv1.domain.usecase.settings

import io.reactivex.rxjava3.core.Single

interface GetServerUrlUseCase {
    operator fun invoke(): Single<String>
}
