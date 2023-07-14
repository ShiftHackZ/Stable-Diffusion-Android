package com.shifthackz.aisdv1.domain.entity

data class Configuration(
    val serverUrl: String,
    val demoMode: Boolean,
    val source: ServerSource,
    val hordeApiKey: String,
)
