package com.shifthackz.aisdv1.domain.usecase.connectivity

import io.reactivex.rxjava3.core.Observable

interface ObserveSeverConnectivityUseCase {
    operator fun invoke(): Observable<Boolean>
}
