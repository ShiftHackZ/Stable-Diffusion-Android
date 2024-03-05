package com.shifthackz.aisdv1.domain.entity

data class Settings(
    val serverUrl: String,
    val demoMode: Boolean,
    val monitorConnectivity: Boolean,
    val autoSaveAiResults: Boolean,
    val saveToMediaStore: Boolean,
    val formAdvancedOptionsAlwaysShow: Boolean,
    val formPromptTaggedInput: Boolean,
    val source: ServerSource,
    val hordeApiKey: String,
    val localUseNNAPI: Boolean,
    val designUseSystemColorPalette: Boolean,
    val designUseSystemDarkTheme: Boolean,
    val designDarkTheme: Boolean,
    val designColorToken: String,
)
