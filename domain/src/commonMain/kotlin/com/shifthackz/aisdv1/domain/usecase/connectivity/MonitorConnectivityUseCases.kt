package com.shifthackz.aisdv1.domain.usecase.connectivity

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

fun interface GetMonitorConnectivityUseCase {
    operator fun invoke(): Boolean
}

fun interface ObserveMonitorConnectivityUseCase {
    operator fun invoke(): Flow<Boolean>
}

object NoOpGetMonitorConnectivityUseCase : GetMonitorConnectivityUseCase {
    override fun invoke() = false
}

object NoOpObserveMonitorConnectivityUseCase : ObserveMonitorConnectivityUseCase {
    override fun invoke() = flowOf(false)
}
