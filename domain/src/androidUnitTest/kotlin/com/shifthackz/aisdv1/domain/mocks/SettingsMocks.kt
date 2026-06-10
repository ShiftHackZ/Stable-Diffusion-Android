package com.shifthackz.aisdv1.domain.mocks

import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.entity.Settings

val mockSettings = Settings(
    serverUrl = "",
    sdModel = "",
    demoMode = false,
    monitorConnectivity = true,
    autoSaveAiResults = true,
    saveToMediaStore = true,
    formAdvancedOptionsAlwaysShow = true,
    formPromptTaggedInput = true,
    source = ServerSource.STABILITY_AI,
    hordeApiKey = "",
    localUseNNAPI = false,
    designUseSystemColorPalette = true,
    designUseSystemDarkTheme = true,
    designDarkTheme = true,
    designColorToken = "",
    designDarkThemeToken = "",
)
