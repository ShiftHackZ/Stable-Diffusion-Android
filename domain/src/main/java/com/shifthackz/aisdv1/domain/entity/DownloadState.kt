package com.shifthackz.aisdv1.domain.entity

import java.io.File

sealed interface DownloadState {
    object Unknown : DownloadState
    data class Downloading(val percent: Int = 0) : DownloadState
    data class Complete(val file: File) : DownloadState
    data class Error(val error: Throwable) : DownloadState
}
