package com.shifthackz.aisdv1.presentation.modal.download

import androidx.compose.runtime.Immutable
import com.shifthackz.android.core.mvi.MviState

@Immutable
data class DownloadDialogState(
    val sources: List<String> = emptyList()
) : MviState
