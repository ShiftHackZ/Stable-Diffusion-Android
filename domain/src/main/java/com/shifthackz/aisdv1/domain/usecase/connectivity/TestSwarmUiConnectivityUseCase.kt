package com.shifthackz.aisdv1.domain.usecase.connectivity

import io.reactivex.rxjava3.core.Completable

interface TestSwarmUiConnectivityUseCase {
    operator fun invoke(url: String): Completable
}
