package com.shifthackz.aisdv1.feature.billing

import com.android.billingclient.api.BillingClient.ProductType
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryProductDetailsParams.Product
import com.android.billingclient.api.QueryPurchasesParams
import com.shifthackz.aisdv1.domain.feature.billing.BillingType

class BillingProductsQueryBuilder {

    private val productRemoveAds = Product
        .newBuilder()
        .setProductId(SKU_REMOVE_ADS)
        .setProductType(ProductType.INAPP)
        .build()

    private val subscriptionMonthly = Product
        .newBuilder()
        .setProductId(SKU_SUBSCRIPTION_MONTHLY)
        .setProductType(ProductType.SUBS)
        .build()

    private val allProducts = listOf(
        productRemoveAds,
        subscriptionMonthly
    )

    fun allProducts() = QueryProductDetailsParams
        .newBuilder()
        .setProductList(allProducts)
        .build()

    fun allPurchases() = QueryPurchasesParams.newBuilder()
        .setProductType(ProductType.INAPP)
        .build()

    fun allSubscriptions() = QueryPurchasesParams.newBuilder()
        .setProductType(ProductType.SUBS)
        .build()

    fun skuByType(type: BillingType) = when (type) {
        BillingType.REMOVE_ADS -> SKU_REMOVE_ADS
    }

    companion object {
        private const val SKU_REMOVE_ADS = "sdai_remove_ads"
        private const val SKU_SUBSCRIPTION_MONTHLY = "sdai_month"
    }
}
