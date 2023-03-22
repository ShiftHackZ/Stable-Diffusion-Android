package com.shifthackz.aisdv1.feature.billing

import com.android.billingclient.api.BillingClient.ProductType
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryProductDetailsParams.Product
import com.android.billingclient.api.QueryPurchasesParams
import com.shifthackz.aisdv1.domain.feature.billing.BillingType

class BillingProductsQueryBuilder {

    private val removeAdsProduct = Product
        .newBuilder()
        .setProductId(SKU_REMOVE_ADS)
        .setProductType(ProductType.INAPP)
        .build()

    fun allProducts() = QueryProductDetailsParams
        .newBuilder()
        .setProductList(listOf(removeAdsProduct))
        .build()

    fun allPurchases() = QueryPurchasesParams.newBuilder()
        .setProductType(ProductType.INAPP)
        .build()

    fun skuByType(type: BillingType) = when (type) {
        BillingType.REMOVE_ADS -> SKU_REMOVE_ADS
    }

    companion object {
        private const val SKU_REMOVE_ADS = "sdai_remove_ads"
    }
}
