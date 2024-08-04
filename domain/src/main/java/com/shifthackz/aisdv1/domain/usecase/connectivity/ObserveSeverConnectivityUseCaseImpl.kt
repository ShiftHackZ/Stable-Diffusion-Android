package com.shifthackz.aisdv1.domain.usecase.connectivity

import com.shifthackz.aisdv1.domain.gateway.ServerConnectivityGateway
import io.reactivex.rxjava3.core.Flowable

internal class ObserveSeverConnectivityUseCaseImpl(
    private val serverConnectivityGateway: ServerConnectivityGateway,
) : ObserveSeverConnectivityUseCase {

    override fun invoke(): Flowable<Boolean> = serverConnectivityGateway
        .observe()
        .distinctUntilChanged()
}
