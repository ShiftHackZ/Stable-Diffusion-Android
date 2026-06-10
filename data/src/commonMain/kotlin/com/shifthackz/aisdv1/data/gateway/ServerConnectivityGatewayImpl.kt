package com.shifthackz.aisdv1.data.gateway

import com.shifthackz.aisdv1.data.provider.ServerUrlProvider
import com.shifthackz.aisdv1.domain.gateway.ServerConnectivityGateway
import com.shifthackz.aisdv1.network.connectivity.ConnectivityMonitor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

internal class ServerConnectivityGatewayImpl(
    private val connectivityMonitor: ConnectivityMonitor,
    private val serverUrlProvider: ServerUrlProvider,
) : ServerConnectivityGateway {

    override fun observe(): Flow<Boolean> = flow {
        val serverUrl = serverUrlProvider("")
        emitAll(connectivityMonitor.observe(serverUrl))
    }
}
