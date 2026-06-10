package com.shifthackz.aisdv1.network.model

import kotlinx.serialization.Serializable

@Serializable
data class SupporterRaw(
    val id: Int?,
    val name: String?,
    val date: String?,
    val amount: String?,
    val currency: String?,
    val type: String?,
    val message: String?,
)
