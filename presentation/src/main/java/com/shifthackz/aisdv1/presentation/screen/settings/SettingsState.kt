package com.shifthackz.aisdv1.presentation.screen.settings

import androidx.compose.runtime.Immutable
import com.shifthackz.aisdv1.domain.entity.ColorToken
import com.shifthackz.aisdv1.domain.entity.DarkThemeToken
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.presentation.model.Modal
import com.shifthackz.android.core.mvi.MviState

@Immutable
data class SettingsState(
    val loading: Boolean = true,
    val screenModal: Modal = Modal.None,
    val serverSource: ServerSource = ServerSource.AUTOMATIC1111,
    val sdModels: List<String> = emptyList(),
    val sdModelSelected: String = "",
    val localUseNNAPI: Boolean = false,
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
    val appVersion: String = "",
    val showLocalUseNNAPI: Boolean = false,
    val showSdModelSelector: Boolean = false,
    val showMonitorConnectionOption: Boolean = false,
    val showFormAdvancedOption: Boolean = false,
    val showUseSystemColorPalette: Boolean = false,
) : MviState
