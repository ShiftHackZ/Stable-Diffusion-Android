package com.shifthackz.aisdv1.presentation.modal.download

import com.shifthackz.android.core.mvi.MviIntent

sealed interface DownloadDialogIntent : MviIntent {

    data class LoadModelData(val id: String): DownloadDialogIntent

    data class SelectSource(val url: String) : DownloadDialogIntent

    data object Close : DownloadDialogIntent

    data object StartDownload : DownloadDialogIntent
}
