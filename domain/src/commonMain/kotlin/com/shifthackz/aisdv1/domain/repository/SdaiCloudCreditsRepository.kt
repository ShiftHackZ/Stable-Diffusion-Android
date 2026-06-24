package com.shifthackz.aisdv1.domain.repository

import com.shifthackz.aisdv1.domain.entity.SdaiCloudTokenBalance
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

interface SdaiCloudCreditsRepository {
    suspend fun getBalance(): SdaiCloudTokenBalance

    fun observeBalance(): Flow<SdaiCloudTokenBalance>
}

object NoOpSdaiCloudCreditsRepository : SdaiCloudCreditsRepository {
    override suspend fun getBalance(): SdaiCloudTokenBalance = SdaiCloudTokenBalance()

    override fun observeBalance(): Flow<SdaiCloudTokenBalance> = flowOf(SdaiCloudTokenBalance())
}
