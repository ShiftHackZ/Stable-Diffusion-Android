package com.shifthackz.aisdv1.domain.gateway

import kotlinx.coroutines.flow.Flow

fun interface ServerConnectivityGateway {
    fun observe(): Flow<Boolean>
}

object NoOpServerConnectivityGateway : ServerConnectivityGateway {
    override fun observe() = kotlinx.coroutines.flow.flowOf(true)
}
