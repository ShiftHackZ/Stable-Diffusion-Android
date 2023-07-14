package com.shifthackz.aisdv1.domain.entity

data class Settings(
    val serverUrl: String,
    val demoMode: Boolean,
    val useSdAiCloud: Boolean,
    val monitorConnectivity: Boolean,
    val autoSaveAiResults: Boolean,
    val formAdvancedOptionsAlwaysShow: Boolean,
    val source: ServerSource,
    val hordeApiKey: String,
)
