package com.shifthackz.aisdv1.domain.usecase.connectivity

import com.shifthackz.aisdv1.domain.gateway.ServerConnectivityGateway
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

fun interface ObserveSeverConnectivityUseCase {
    operator fun invoke(): Flow<Boolean>
}

class ObserveSeverConnectivityUseCaseImpl(
    private val serverConnectivityGateway: ServerConnectivityGateway,
) : ObserveSeverConnectivityUseCase {

    override fun invoke() = serverConnectivityGateway
        .observe()
        .distinctUntilChanged()
}
