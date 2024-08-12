package com.shifthackz.aisdv1.domain.entity

import com.shifthackz.aisdv1.core.common.schedulers.SchedulersToken

data class Settings(
    val serverUrl: String = "",
    val sdModel: String = "",
    val demoMode: Boolean = false,
    val developerMode: Boolean = false,
    val localDiffusionAllowCancel: Boolean = false,
    val localDiffusionSchedulerThread: SchedulersToken = SchedulersToken.COMPUTATION,
    val monitorConnectivity: Boolean = false,
    val backgroundGeneration: Boolean = false,
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
    val galleryGrid: Grid = Grid.Fixed2,
)
