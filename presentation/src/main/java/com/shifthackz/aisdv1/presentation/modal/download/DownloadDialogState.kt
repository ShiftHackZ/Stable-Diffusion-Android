package com.shifthackz.aisdv1.presentation.modal.download

import androidx.compose.runtime.Immutable
import com.shifthackz.android.core.mvi.MviState

@Immutable
data class DownloadDialogState(
    val sources: List<Pair<String, Boolean>> = emptyList(),
) : MviState {

    val selectedUrl: String
        get() = sources.find { (_, selected) -> selected }?.first ?: ""
}
