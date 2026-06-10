package com.shifthackz.aisdv1.domain.usecase.connectivity

import com.shifthackz.aisdv1.domain.gateway.ServerConnectivityGateway
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

/**
 * Executes the `function` step in the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
fun interface ObserveSeverConnectivityUseCase {
    operator fun invoke(): Flow<Boolean>
}

/**
 * Implements `ObserveSeverConnectivityUseCase` behavior in the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
class ObserveSeverConnectivityUseCaseImpl(
    /**
     * Exposes the `serverConnectivityGateway` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val serverConnectivityGateway: ServerConnectivityGateway,
) : ObserveSeverConnectivityUseCase {

    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    override fun invoke() = serverConnectivityGateway
        .observe()
        .distinctUntilChanged()
}
