package com.shifthackz.aisdv1.domain.usecase.settings

import io.reactivex.rxjava3.core.Single

interface GetConfigurationUseCase {
    operator fun invoke(): Single<Pair<String, Boolean>>
}
