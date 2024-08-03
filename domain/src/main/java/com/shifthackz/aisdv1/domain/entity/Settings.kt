package com.shifthackz.aisdv1.domain.entity

data class Settings(
    val serverUrl: String = "",
    val sdModel: String = "",
    val demoMode: Boolean = false,
    val monitorConnectivity: Boolean = false,
    val autoSaveAiResults: Boolean = false,
    val saveToMediaStore: Boolean = false,
    val formAdvancedOptionsAlwaysShow: Boolean = false,
    val formPromptTaggedInput: Boolean = false,
    val source: ServerSource = ServerSource.AUTOMATIC1111,
    val hordeApiKey: String = "",
    val localUseNNAPI: Boolean = false,
    val designUseSystemColorPalette: Boolean = false,
    val designUseSystemDarkTheme: Boolean = false,
    val designDarkTheme: Boolean = false,
    val designColorToken: String = "",
    val designDarkThemeToken: String = "",
)
