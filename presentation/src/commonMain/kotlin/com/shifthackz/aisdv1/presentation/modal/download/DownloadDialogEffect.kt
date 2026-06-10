package com.shifthackz.aisdv1.presentation.modal.download

import com.shifthackz.aisdv1.core.mvi.MviEffect

/**
 * Defines the `DownloadDialogEffect` contract for the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
sealed interface DownloadDialogEffect : MviEffect {

    /**
     * Provides the `Close` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object Close : DownloadDialogEffect

    /**
     * Carries `StartDownload` data through the SDAI presentation layer.
     *
     * @param url remote URL used by the operation.
     * @author Dmitriy Moroz
     */
    data class StartDownload(val url: String) : DownloadDialogEffect
}
