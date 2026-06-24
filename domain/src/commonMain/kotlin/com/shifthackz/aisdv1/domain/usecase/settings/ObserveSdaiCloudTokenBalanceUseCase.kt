package com.shifthackz.aisdv1.domain.usecase.settings

import com.shifthackz.aisdv1.domain.entity.SdaiCloudTokenBalance
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

interface ObserveSdaiCloudTokenBalanceUseCase {
    operator fun invoke(): Flow<SdaiCloudTokenBalanceState>

    fun refresh()
}

sealed interface SdaiCloudTokenBalanceState {
    data object Inactive : SdaiCloudTokenBalanceState
    data object Loading : SdaiCloudTokenBalanceState
    data class Ready(val balance: SdaiCloudTokenBalance) : SdaiCloudTokenBalanceState
    data class Failed(val error: Throwable) : SdaiCloudTokenBalanceState
}

object NoOpObserveSdaiCloudTokenBalanceUseCase : ObserveSdaiCloudTokenBalanceUseCase {
    override fun invoke(): Flow<SdaiCloudTokenBalanceState> =
        flowOf(SdaiCloudTokenBalanceState.Inactive)

    override fun refresh() = Unit
}
