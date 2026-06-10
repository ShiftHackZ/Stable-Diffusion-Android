package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.domain.entity.DownloadState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

internal interface DownloadableModelFileDownloader {
    fun download(
        id: String,
        url: String,
    ): Flow<DownloadState>
}

internal object NoOpDownloadableModelFileDownloader : DownloadableModelFileDownloader {

    override fun download(
        id: String,
        url: String,
    ): Flow<DownloadState> = flowOf(
        DownloadState.Error(
            UnsupportedOperationException("Local model download is unavailable on this platform."),
        ),
    )
}
