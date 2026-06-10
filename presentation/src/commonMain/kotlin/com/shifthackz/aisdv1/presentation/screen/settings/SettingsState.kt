package com.shifthackz.aisdv1.presentation.screen.settings

import androidx.compose.runtime.Immutable
import com.shifthackz.aisdv1.domain.entity.ColorToken
import com.shifthackz.aisdv1.domain.entity.DarkThemeToken
import com.shifthackz.aisdv1.domain.entity.Grid
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.core.mvi.MviState

@Immutable
data class SettingsState(
    val loading: Boolean = true,
    val onBoardingDemo: Boolean = false,
    val screenModal: SettingsModal = SettingsModal.None,
    val serverSource: ServerSource = ServerSource.AUTOMATIC1111,
    val sdModels: List<String> = emptyList(),
    val sdModelSelected: String = "",
    val stabilityAiCredits: Float = 0f,
    val localUseNNAPI: Boolean = false,
    val backgroundGenerationAvailable: Boolean = false,
    val backgroundGeneration: Boolean = false,
    val monitorConnectivity: Boolean = false,
    val autoSaveAiResults: Boolean = false,
    val saveToMediaStore: Boolean = false,
    val formAdvancedOptionsAlwaysShow: Boolean = false,
    val formPromptTaggedInput: Boolean = false,
    val useSystemColorPalette: Boolean = false,
    val useSystemDarkTheme: Boolean = false,
    val darkTheme: Boolean = false,
    val colorToken: ColorToken = ColorToken.MAUVE,
    val darkThemeToken: DarkThemeToken = DarkThemeToken.FRAPPE,
    val galleryGrid: Grid = Grid.Fixed2,
    val developerMode: Boolean = false,
    val appVersion: String = "",
) : MviState {

    val showStabilityAiCredits: Boolean
        get() = serverSource == ServerSource.STABILITY_AI

    val showLocalMicrosoftONNXUseNNAPI: Boolean
        get() = serverSource == ServerSource.LOCAL_MICROSOFT_ONNX

    val showSdModelSelector: Boolean
        get() = serverSource == ServerSource.AUTOMATIC1111

    val showMonitorConnectionOption: Boolean
        get() = serverSource == ServerSource.AUTOMATIC1111 || serverSource == ServerSource.SWARM_UI

    val showFormAdvancedOption: Boolean
        get() = serverSource != ServerSource.OPEN_AI

    val showUseSystemColorPalette: Boolean = false
}

sealed interface SettingsModal {
    data object None : SettingsModal
    data object ClearAppCache : SettingsModal
    data object Communicating : SettingsModal
    data object Language : SettingsModal
    data class SelectSdModel(val models: List<String>, val selected: String) : SettingsModal
    data class ManualPermission(val permission: String) : SettingsModal
    data class GalleryGrid(val grid: Grid) : SettingsModal
}
