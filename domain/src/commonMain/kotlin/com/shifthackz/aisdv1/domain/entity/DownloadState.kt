package com.shifthackz.aisdv1.domain.entity

sealed interface DownloadState {
    data object Unknown : DownloadState
    data class Downloading(val percent: Int = 0) : DownloadState
    data class Complete(val filePath: String) : DownloadState
    data class Error(val error: Throwable) : DownloadState
}
