package com.shifthackz.aisdv1.presentation.modal.download

import com.shifthackz.aisdv1.core.mvi.MviIntent

sealed interface DownloadDialogIntent : MviIntent {

    data class SelectSource(val url: String) : DownloadDialogIntent

    data object Close : DownloadDialogIntent

    data object StartDownload : DownloadDialogIntent
}
