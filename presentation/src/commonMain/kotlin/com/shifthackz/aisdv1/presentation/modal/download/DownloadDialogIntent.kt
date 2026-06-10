package com.shifthackz.aisdv1.presentation.modal.download

import com.shifthackz.aisdv1.core.mvi.MviIntent

/**
 * Defines the `DownloadDialogIntent` contract for the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
sealed interface DownloadDialogIntent : MviIntent {

    /**
     * Carries `SelectSource` data through the SDAI presentation layer.
     *
     * @param url remote URL used by the operation.
     * @author Dmitriy Moroz
     */
    data class SelectSource(val url: String) : DownloadDialogIntent

    /**
     * Provides the `Close` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object Close : DownloadDialogIntent

    /**
     * Provides the `StartDownload` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object StartDownload : DownloadDialogIntent
}
