package com.shifthackz.aisdv1.domain.repository

import com.shifthackz.aisdv1.domain.entity.SdaiCloudIapProduct
import com.shifthackz.aisdv1.domain.entity.SdaiCloudTokenBalance

interface SdaiCloudTopUpRepository {
    suspend fun getIapProducts(): List<SdaiCloudIapProduct>
    suspend fun topUpWithRewardedAd(): SdaiCloudTokenBalance
    suspend fun topUpWithIap(productId: String): SdaiCloudTokenBalance
    suspend fun restoreIapPurchases(): SdaiCloudTokenBalance
}

object NoOpSdaiCloudTopUpRepository : SdaiCloudTopUpRepository {
    override suspend fun getIapProducts(): List<SdaiCloudIapProduct> = emptyList()

    override suspend fun topUpWithRewardedAd(): SdaiCloudTokenBalance =
        error("SDAI Cloud top-up is unavailable in this build.")

    override suspend fun topUpWithIap(productId: String): SdaiCloudTokenBalance =
        error("SDAI Cloud top-up is unavailable in this build.")

    override suspend fun restoreIapPurchases(): SdaiCloudTokenBalance =
        error("SDAI Cloud top-up is unavailable in this build.")
}
