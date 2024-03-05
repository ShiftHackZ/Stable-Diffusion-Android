package com.shifthackz.aisdv1.domain.gateway

import io.reactivex.rxjava3.core.Flowable

fun interface ServerConnectivityGateway {
    fun observe(): Flowable<Boolean>
}
