package com.shifthackz.aisdv1.presentation.screen.settings

import androidx.compose.runtime.Immutable
import com.shifthackz.aisdv1.presentation.model.Modal
import com.shifthackz.android.core.mvi.MviState

@Immutable
data class SettingsState(
    val loading: Boolean = true,
    val screenModal: Modal = Modal.None,
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

    // --
    val appVersion: String = "",
    // --
    val showLocalUseNNAPI: Boolean = false,
    val showSdModelSelector: Boolean = false,
    val showMonitorConnectionOption: Boolean = false,
    val showFormAdvancedOption: Boolean = false,
    val showUseSystemColorPalette: Boolean = false,
) : MviState
