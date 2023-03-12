package com.shifthackz.aisdv1.domain.gateway

import io.reactivex.rxjava3.core.Observable

fun interface ServerConnectivityGateway {
    fun observe(): Observable<Boolean>
}
