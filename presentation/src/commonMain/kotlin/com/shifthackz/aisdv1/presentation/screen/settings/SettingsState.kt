package com.shifthackz.aisdv1.presentation.screen.settings

import androidx.compose.runtime.Immutable
import com.shifthackz.aisdv1.domain.entity.ColorToken
import com.shifthackz.aisdv1.domain.entity.DarkThemeToken
import com.shifthackz.aisdv1.domain.entity.Grid
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.core.mvi.MviState

/**
 * Carries `SettingsState` data through the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
@Immutable
data class SettingsState(
    /**
     * Exposes the `loading` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val loading: Boolean = true,
    /**
     * Exposes the `onBoardingDemo` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val onBoardingDemo: Boolean = false,
    /**
     * Exposes the `screenModal` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val screenModal: SettingsModal = SettingsModal.None,
    /**
     * Exposes the `serverSource` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val serverSource: ServerSource = ServerSource.AUTOMATIC1111,
    /**
     * Exposes the `sdModels` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val sdModels: List<String> = emptyList(),
    /**
     * Exposes the `sdModelSelected` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val sdModelSelected: String = "",
    /**
     * Exposes the `stabilityAiCredits` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val stabilityAiCredits: Float = 0f,
    /**
     * Exposes the `localUseNNAPI` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val localUseNNAPI: Boolean = false,
    /**
     * Exposes the `backgroundGenerationAvailable` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val backgroundGenerationAvailable: Boolean = false,
    /**
     * Exposes the `backgroundGeneration` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val backgroundGeneration: Boolean = false,
    /**
     * Exposes the `monitorConnectivity` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val monitorConnectivity: Boolean = false,
    /**
     * Exposes the `autoSaveAiResults` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val autoSaveAiResults: Boolean = false,
    /**
     * Exposes the `saveToMediaStore` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val saveToMediaStore: Boolean = false,
    /**
     * Exposes the `formAdvancedOptionsAlwaysShow` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val formAdvancedOptionsAlwaysShow: Boolean = false,
    /**
     * Exposes the `formPromptTaggedInput` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val formPromptTaggedInput: Boolean = false,
    /**
     * Exposes the `useSystemColorPalette` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val useSystemColorPalette: Boolean = false,
    /**
     * Exposes the `useSystemDarkTheme` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val useSystemDarkTheme: Boolean = false,
    /**
     * Exposes the `darkTheme` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val darkTheme: Boolean = false,
    /**
     * Exposes the `colorToken` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val colorToken: ColorToken = ColorToken.MAUVE,
    /**
     * Exposes the `darkThemeToken` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val darkThemeToken: DarkThemeToken = DarkThemeToken.FRAPPE,
    /**
     * Exposes the `galleryGrid` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val galleryGrid: Grid = Grid.Fixed2,
    /**
     * Exposes the `developerMode` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val developerMode: Boolean = false,
    /**
     * Exposes the `appVersion` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
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
 * Defines the `SettingsModal` contract for the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
sealed interface SettingsModal {
    /**
     * Provides the `None` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object None : SettingsModal
    /**
     * Provides the `ClearAppCache` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object ClearAppCache : SettingsModal
    /**
     * Provides the `Communicating` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object Communicating : SettingsModal
    /**
     * Provides the `Language` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object Language : SettingsModal
    /**
     * Carries `SelectSdModel` data through the SDAI presentation layer.
     *
     * @param models models value consumed by the API.
     * @param selected selected value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class SelectSdModel(val models: List<String>, val selected: String) : SettingsModal
    /**
     * Carries `ManualPermission` data through the SDAI presentation layer.
     *
     * @param permission permission value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class ManualPermission(val permission: String) : SettingsModal
    /**
     * Carries `GalleryGrid` data through the SDAI presentation layer.
     *
     * @param grid grid value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class GalleryGrid(val grid: Grid) : SettingsModal
}
