package com.shifthackz.aisdv1.presentation.screen.settings

import com.shifthackz.aisdv1.core.ui.MviEffect
import com.shifthackz.aisdv1.core.ui.MviState

sealed interface SettingsEffect : MviEffect {
    data object RequestStoragePermission : SettingsEffect
}

sealed interface SettingsState : MviState {

    val screenDialog: Dialog

    data object Uninitialized : SettingsState {
        override val screenDialog = Dialog.None
    }

    data class Content(
        override val screenDialog: Dialog = Dialog.None,
        val sdModels: List<String>,
        val sdModelSelected: String,
        val localUseNNAPI: Boolean,
        val monitorConnectivity: Boolean,
        val autoSaveAiResults: Boolean,
        val saveToMediaStore: Boolean,
        val formAdvancedOptionsAlwaysShow: Boolean,
        val appVersion: String,
        val showLocalUseNNAPI: Boolean,
        val showSdModelSelector: Boolean,
        val showMonitorConnectionOption: Boolean,
    ) : SettingsState

    fun withDialog(value: Dialog): SettingsState = when (this) {
        is Content -> copy(screenDialog = value)
        else -> this
    }

    sealed interface Dialog {
        data object None : Dialog
        data object Communicating : Dialog
        data object ClearAppCache : Dialog
        data class SelectSdModel(val models: List<String>, val selected: String) : Dialog
    }
}
