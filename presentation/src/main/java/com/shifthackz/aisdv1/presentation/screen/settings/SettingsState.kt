package com.shifthackz.aisdv1.presentation.screen.settings

import androidx.compose.runtime.Immutable
import com.shifthackz.android.core.mvi.MviState

@Immutable
data class SettingsState(
    val loading: Boolean = true,
    val screenDialog: Dialog = Dialog.None,
    val sdModels: List<String> = emptyList(),
    val sdModelSelected: String = "",
    val localUseNNAPI: Boolean = false,
    val monitorConnectivity: Boolean = false,
    val autoSaveAiResults: Boolean = false,
    val saveToMediaStore: Boolean = false,
    val formAdvancedOptionsAlwaysShow: Boolean = false,
    val appVersion: String = "",
    val showLocalUseNNAPI: Boolean = false,
    val showSdModelSelector: Boolean = false,
    val showMonitorConnectionOption: Boolean = false,
    val showFormAdvancedOption: Boolean = false,
) : MviState {

    sealed interface Dialog {
        data object None : Dialog
        data object Communicating : Dialog
        data object ClearAppCache : Dialog
        data class SelectSdModel(val models: List<String>, val selected: String) : Dialog
    }
}
