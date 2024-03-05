package com.shifthackz.aisdv1.domain.usecase.connectivity

import io.reactivex.rxjava3.core.Flowable

interface ObserveSeverConnectivityUseCase {
    operator fun invoke(): Flowable<Boolean>
}
