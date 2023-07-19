package com.shifthackz.aisdv1.presentation.screen.settings

import com.shifthackz.aisdv1.core.ui.MviEffect
import com.shifthackz.aisdv1.core.ui.MviState

sealed interface SettingsEffect : MviEffect {
    object RequestStoragePermission : SettingsEffect
}

sealed interface SettingsState : MviState {

    val screenDialog: Dialog
    val bottomSheet: Sheet

    object Uninitialized : SettingsState {
        override val screenDialog = Dialog.None
        override val bottomSheet = Sheet.None
    }

    data class Content(
        override val screenDialog: Dialog = Dialog.None,
        override val bottomSheet: Sheet = Sheet.None,
        val sdModels: List<String>,
        val sdModelSelected: String,
        val monitorConnectivity: Boolean,
        val autoSaveAiResults: Boolean,
        val saveToMediaStore: Boolean,
        val formAdvancedOptionsAlwaysShow: Boolean,
        val appVersion: String,
        val showRewardedSdAiAd: Boolean,
        val showSdModelSelector: Boolean,
        val showMonitorConnectionOption: Boolean,
        val showRateGooglePlay: Boolean,
        val showGitHubLink: Boolean,
    ) : SettingsState

    fun withDialog(value: Dialog): SettingsState = when (this) {
        is Content -> copy(screenDialog = value)
        else -> this
    }

    fun withSheet(value: Sheet): SettingsState = when (this) {
        is Content -> copy(bottomSheet = value)
        else -> this
    }

    sealed interface Dialog {
        object None : Dialog
        object Communicating : Dialog
        object ClearAppCache : Dialog
        data class SelectSdModel(val models: List<String>, val selected: String) : Dialog
    }

    sealed interface Sheet {
        object None : Sheet
        object SelectLanguage : Sheet
    }
}
