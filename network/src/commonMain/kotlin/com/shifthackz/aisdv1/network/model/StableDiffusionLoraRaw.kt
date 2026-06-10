package com.shifthackz.aisdv1.network.model

import kotlinx.serialization.Serializable

@Serializable
data class StableDiffusionLoraRaw(
    val name: String? = null,
    val alias: String? = null,
    val path: String? = null,
)
