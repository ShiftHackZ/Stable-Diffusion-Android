package com.shifthackz.aisdv1.network.response

import kotlinx.serialization.Serializable

@Serializable
data class DownloadableModelResponse(
    val id: String?,
    val name: String?,
    val size: String?,
    val sources: List<String>?,
)
