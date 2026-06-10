package com.shifthackz.aisdv1.domain.entity

/**
 * Defines the `DownloadState` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
sealed interface DownloadState {
    /**
     * Provides the `Unknown` singleton used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    data object Unknown : DownloadState
    /**
     * Carries `Downloading` data through the SDAI domain layer.
     *
     * @param percent percent value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class Downloading(val percent: Int = 0) : DownloadState
    /**
     * Carries `Complete` data through the SDAI domain layer.
     *
     * @param filePath file path value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class Complete(val filePath: String) : DownloadState
    /**
     * Carries `Error` data through the SDAI domain layer.
     *
     * @param error error value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class Error(val error: Throwable) : DownloadState
}
