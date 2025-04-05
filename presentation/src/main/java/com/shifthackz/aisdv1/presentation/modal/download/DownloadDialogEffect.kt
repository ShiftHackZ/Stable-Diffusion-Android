package com.shifthackz.aisdv1.presentation.modal.download

import com.shifthackz.android.core.mvi.MviEffect

sealed interface DownloadDialogEffect : MviEffect {

    data object Close : DownloadDialogEffect

    data class Select(val url: String) : DownloadDialogEffect
}
