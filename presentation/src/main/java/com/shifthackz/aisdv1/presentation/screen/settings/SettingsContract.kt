package com.shifthackz.aisdv1.presentation.screen.settings

import com.shifthackz.aisdv1.core.ui.MviState

interface SettingsState : MviState {

    val screenDialog: Dialog

    object Uninitialized : SettingsState {
        override val screenDialog = Dialog.None
    }

    data class Content(
        override val screenDialog: Dialog = Dialog.None,
        val sdModels: List<String>,
        val sdModelSelected: String,
        val appVersion: String,
    ) : SettingsState

    fun withDialog(value: Dialog): SettingsState = when (this) {
        is Content -> copy(screenDialog = value)
        else -> this
    }

    sealed interface Dialog {
        object None : Dialog
        object Communicating : Dialog
        object ClearAppCache : Dialog
        data class SelectSdModel(val models: List<String>, val selected: String) : Dialog
    }
}
