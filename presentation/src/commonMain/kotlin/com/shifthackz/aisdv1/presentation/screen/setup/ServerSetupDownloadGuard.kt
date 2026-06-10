package com.shifthackz.aisdv1.presentation.screen.setup

/**
 * Defines the `ServerSetupDownloadGuard` contract for the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
interface ServerSetupDownloadGuard {
    /**
     * Executes the `withDownload` step in the SDAI presentation layer.
     *
     * @param block block value consumed by the API.
     * @return Result produced by `withDownload`.
     * @author Dmitriy Moroz
     */
    suspend fun <T> withDownload(block: suspend () -> T): T
}

/**
 * Provides the `NoOpServerSetupDownloadGuard` singleton used by the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
object NoOpServerSetupDownloadGuard : ServerSetupDownloadGuard {
    /**
     * Executes the `withDownload` step in the SDAI presentation layer.
     *
     * @param block block value consumed by the API.
     * @author Dmitriy Moroz
     */
    override suspend fun <T> withDownload(block: suspend () -> T): T = block()
}
