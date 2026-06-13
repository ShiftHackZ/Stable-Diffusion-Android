package com.shifthackz.aisdv1.presentation.screen.settings.model

import androidx.compose.runtime.Immutable
import com.shifthackz.aisdv1.core.mvi.MviState
import com.shifthackz.aisdv1.domain.entity.ColorToken
import com.shifthackz.aisdv1.domain.entity.DarkThemeToken
import com.shifthackz.aisdv1.domain.entity.Grid
import com.shifthackz.aisdv1.domain.entity.ServerSource

/**
 * Immutable UI model for the Settings screen.
 *
 * The fields mirror persisted app preferences, runtime provider status, and the
 * currently visible modal. Derived `show...` properties keep section visibility
 * rules close to the state they depend on.
 *
 * @author Dmitriy Moroz
 */
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

/**
 * Modal destinations that can be displayed on top of the Settings screen.
 *
 * @author Dmitriy Moroz
 */
sealed interface SettingsModal {
    /**
     * No modal is visible.
     *
     * @author Dmitriy Moroz
     */
    data object None : SettingsModal
    /**
     * Confirmation dialog before deleting local app cache.
     *
     * @author Dmitriy Moroz
     */
    data object ClearAppCache : SettingsModal
    /**
     * Blocking progress dialog while a long settings action is running.
     *
     * @author Dmitriy Moroz
     */
    data object Communicating : SettingsModal
    /**
     * Bottom sheet for choosing the app language.
     *
     * @author Dmitriy Moroz
     */
    data object Language : SettingsModal
    data class SelectSdModel(val models: List<String>, val selected: String) : SettingsModal
    data class ManualPermission(val permission: String) : SettingsModal
    data class GalleryGrid(val grid: Grid) : SettingsModal
}
