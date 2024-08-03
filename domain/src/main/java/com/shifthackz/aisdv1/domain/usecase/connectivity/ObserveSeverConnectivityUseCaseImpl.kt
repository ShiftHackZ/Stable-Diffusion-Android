package com.shifthackz.aisdv1.domain.usecase.connectivity

import com.shifthackz.aisdv1.domain.gateway.ServerConnectivityGateway

internal class ObserveSeverConnectivityUseCaseImpl(
    private val serverConnectivityGateway: ServerConnectivityGateway,
) : ObserveSeverConnectivityUseCase {

    override fun invoke() = serverConnectivityGateway
        .observe()
        .distinctUntilChanged()
}
