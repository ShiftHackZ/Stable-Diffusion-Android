package com.shifthackz.aisdv1.network.model

import kotlinx.serialization.Serializable

@Serializable
data class StableDiffusionHyperNetworkRaw(
    val name: String? = null,
    val path: String? = null,
)
