package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.domain.entity.DownloadState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * Defines the `DownloadableModelFileDownloader` contract for the SDAI data layer.
 *
 * @author Dmitriy Moroz
 */
internal interface DownloadableModelFileDownloader {
    /**
     * Executes the `download` step in the SDAI data layer.
     *
     * @param id identifier of the target entity.
     * @param url remote URL used by the operation.
     * @return Result produced by `download`.
     * @author Dmitriy Moroz
     */
    fun download(
        id: String,
        url: String,
    ): Flow<DownloadState>
}

/**
 * Provides the `NoOpDownloadableModelFileDownloader` singleton used by the SDAI data layer.
 *
 * @author Dmitriy Moroz
 */
internal object NoOpDownloadableModelFileDownloader : DownloadableModelFileDownloader {

    /**
     * Executes the `download` step in the SDAI data layer.
     *
     * @param id identifier of the target entity.
     * @param url remote URL used by the operation.
     * @author Dmitriy Moroz
     */
    override fun download(
        id: String,
        url: String,
    ): Flow<DownloadState> = flowOf(
        DownloadState.Error(
            UnsupportedOperationException("Local model download is unavailable on this platform."),
        ),
    )
}
