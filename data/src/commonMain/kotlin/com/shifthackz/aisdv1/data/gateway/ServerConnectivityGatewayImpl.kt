package com.shifthackz.aisdv1.data.gateway

import com.shifthackz.aisdv1.data.provider.ServerUrlProvider
import com.shifthackz.aisdv1.domain.gateway.ServerConnectivityGateway
import com.shifthackz.aisdv1.network.connectivity.ConnectivityMonitor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

/**
 * Implements `ServerConnectivityGateway` behavior in the SDAI data layer.
 *
 * @author Dmitriy Moroz
 */
internal class ServerConnectivityGatewayImpl(
    /**
     * Exposes the `connectivityMonitor` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private val connectivityMonitor: ConnectivityMonitor,
    /**
     * Exposes the `serverUrlProvider` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private val serverUrlProvider: ServerUrlProvider,
) : ServerConnectivityGateway {

    /**
     * Loads SDAI data through `observe`.
     *
     * @return Result produced by `observe`.
     * @author Dmitriy Moroz
     */
    override fun observe(): Flow<Boolean> = flow {
        val serverUrl = serverUrlProvider("")
        emitAll(connectivityMonitor.observe(serverUrl))
    }
}
