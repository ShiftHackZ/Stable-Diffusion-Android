package com.shifthackz.aisdv1.domain.gateway

import kotlinx.coroutines.flow.Flow

/**
 * Executes the `function` step in the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
fun interface ServerConnectivityGateway {
    fun observe(): Flow<Boolean>
}

/**
 * Provides the `NoOpServerConnectivityGateway` singleton used by the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
object NoOpServerConnectivityGateway : ServerConnectivityGateway {
    /**
     * Loads SDAI data through `observe`.
     *
     * @author Dmitriy Moroz
     */
    override fun observe() = kotlinx.coroutines.flow.flowOf(true)
}
