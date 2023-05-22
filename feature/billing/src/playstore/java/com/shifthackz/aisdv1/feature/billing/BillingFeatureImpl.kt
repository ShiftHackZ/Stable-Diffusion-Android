package com.shifthackz.aisdv1.feature.billing

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.shifthackz.aisdv1.core.common.log.debugLog
import com.shifthackz.aisdv1.core.common.log.infoLog
import com.shifthackz.aisdv1.domain.feature.billing.BillingFeature
import com.shifthackz.aisdv1.domain.feature.billing.BillingType

class BillingFeatureImpl : BillingFeature, BillingClientStateListener, PurchasesUpdatedListener {

    private var billingClient: BillingClient? = null

    private val queryBuilder = BillingProductsQueryBuilder()
    private val products = arrayListOf<ProductDetails>()

    override fun initialize(context: Context) {
        debugLog("Initializing billing feature...")
        billingClient = BillingClient.newBuilder(context)
            .setListener(this)
            .enablePendingPurchases()
            .build()
            .also { client -> client.startConnection(this) }
    }

    override fun launchPurchase(activity: Activity, type: BillingType) {
        val sku = queryBuilder.skuByType(type)
        val product = products.firstOrNull { it.productId == sku }
        product?.let {
            val productDetailsParams = listOf(
                BillingFlowParams.ProductDetailsParams.newBuilder()
                    .setProductDetails(it)
                    .build()
            )

            val billingLauncherParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(productDetailsParams)
                .build()

            billingClient?.launchBillingFlow(activity, billingLauncherParams)
        }
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: MutableList<Purchase>?) {
        if (billingResult.responseCode != BillingResponseCode.OK && purchases != null) {
            purchases.forEach {
                handlePurchase(it)
            }
        }
    }

    override fun onBillingServiceDisconnected() {
        infoLog("Disconnected from GooglePlay billing... Retrying connection...")
        billingClient?.startConnection(this)
    }

    override fun onBillingSetupFinished(billingResult: BillingResult) {
        if (billingResult.responseCode == BillingResponseCode.OK) {
            infoLog("Connected to GooglePlay billing!")
            queryProducts()
            queryPurchases()
            querySubscriptions()
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        debugLog("PURCHASE : $purchase")
    }

    private fun queryProducts() = billingClient?.queryProductDetailsAsync(
        queryBuilder.allProducts(),
    ) { billingResult, products ->
        if (billingResult.responseCode == BillingResponseCode.OK) {
            this.products.clear()
            this.products.addAll(products)
            debugLog("----------------------------------------")
            products.forEach {
                debugLog("Product : $it")
            }
            debugLog("----------------------------------------")
        }
    }

    private fun queryPurchases() = billingClient?.queryPurchasesAsync(
        queryBuilder.allPurchases()
    ) { billingResult, purchases ->
        purchases.forEach { purchase ->
            handlePurchase(purchase)
        }
    }

    private fun querySubscriptions() = billingClient?.queryPurchasesAsync(
        queryBuilder.allSubscriptions()
    ) { billingResult, purchases ->
        purchases.forEach { purchase ->
            handlePurchase(purchase)
        }
    }
}
