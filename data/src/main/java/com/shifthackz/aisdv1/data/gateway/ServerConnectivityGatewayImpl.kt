package com.shifthackz.aisdv1.data.gateway

import com.shifthackz.aisdv1.data.provider.ServerUrlProvider
import com.shifthackz.aisdv1.domain.gateway.ServerConnectivityGateway
import com.shifthackz.aisdv1.network.connectivity.ConnectivityMonitor
import io.reactivex.rxjava3.core.Observable

internal class ServerConnectivityGatewayImpl(
    private val connectivityMonitor: ConnectivityMonitor,
    private val serverUrlProvider: ServerUrlProvider,
) : ServerConnectivityGateway {

    override fun observe(): Observable<Boolean> = serverUrlProvider("")
        .flatMapObservable(connectivityMonitor::observe)
}
