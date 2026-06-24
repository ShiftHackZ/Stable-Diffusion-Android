package com.shifthackz.aisdv1.domain.entity

data class SdaiCloudIapProduct(
    val productId: String,
    val title: String,
    val description: String,
    val formattedPrice: String,
    val tokenAmount: Int,
)
