package com.shifthackz.aisdv1.core.sdai

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

data class SdaiCloudPurchaseProductUi(
    val productId: String,
    val title: String,
    val description: String,
    val formattedPrice: String?,
    val tokenAmount: Int,
)

interface SdaiCloudUi {
    @Composable
    fun TokenBalance(
        onBuyClick: () -> Unit,
        modifier: Modifier,
    )

    @Composable
    fun RequiredTopUpDialog(
        onDismissRequest: () -> Unit,
        onRewardedAdRequest: () -> Unit,
        onIapProductsRequest: () -> Unit,
    )

    @Composable
    fun LoadingProductsDialog()

    @Composable
    fun PurchaseSheet(
        products: List<SdaiCloudPurchaseProductUi>,
        restoreAvailable: Boolean,
        onDismissRequest: () -> Unit,
        onIapRequest: (String) -> Unit,
        onRestorePurchasesRequest: () -> Unit,
    )

    @Composable
    fun WorkingDialog()
}

object NoOpSdaiCloudUi : SdaiCloudUi {
    @Composable
    override fun TokenBalance(
        onBuyClick: () -> Unit,
        modifier: Modifier,
    ) = Unit

    @Composable
    override fun RequiredTopUpDialog(
        onDismissRequest: () -> Unit,
        onRewardedAdRequest: () -> Unit,
        onIapProductsRequest: () -> Unit,
    ) = Unit

    @Composable
    override fun LoadingProductsDialog() = Unit

    @Composable
    override fun PurchaseSheet(
        products: List<SdaiCloudPurchaseProductUi>,
        restoreAvailable: Boolean,
        onDismissRequest: () -> Unit,
        onIapRequest: (String) -> Unit,
        onRestorePurchasesRequest: () -> Unit,
    ) = Unit

    @Composable
    override fun WorkingDialog() = Unit
}
