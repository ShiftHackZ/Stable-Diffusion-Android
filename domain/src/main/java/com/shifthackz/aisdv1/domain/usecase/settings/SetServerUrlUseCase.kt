package com.shifthackz.aisdv1.domain.usecase.settings

import io.reactivex.rxjava3.core.Completable

interface SetServerUrlUseCase {
    operator fun invoke(url: String): Completable
}
