package com.shifthackz.aisdv1.domain.usecase.connectivity

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * Executes the `function` step in the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
fun interface GetMonitorConnectivityUseCase {
    operator fun invoke(): Boolean
}

/**
 * Executes the `function` step in the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
fun interface ObserveMonitorConnectivityUseCase {
    operator fun invoke(): Flow<Boolean>
}

/**
 * Provides the `NoOpGetMonitorConnectivityUseCase` singleton used by the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
object NoOpGetMonitorConnectivityUseCase : GetMonitorConnectivityUseCase {
    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    override fun invoke() = false
}

/**
 * Provides the `NoOpObserveMonitorConnectivityUseCase` singleton used by the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
object NoOpObserveMonitorConnectivityUseCase : ObserveMonitorConnectivityUseCase {
    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    override fun invoke() = flowOf(false)
}
