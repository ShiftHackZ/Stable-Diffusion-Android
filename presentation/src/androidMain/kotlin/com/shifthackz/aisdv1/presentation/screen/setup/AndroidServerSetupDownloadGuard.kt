package com.shifthackz.aisdv1.presentation.screen.setup

import com.shifthackz.aisdv1.domain.interactor.wakelock.WakeLockInterActor

/**
 * Coordinates `AndroidServerSetupDownloadGuard` behavior in the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
internal class AndroidServerSetupDownloadGuard(
    /**
     * Exposes the `wakeLockInterActor` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val wakeLockInterActor: WakeLockInterActor,
) : ServerSetupDownloadGuard {

    /**
     * Executes the `withDownload` step in the SDAI presentation layer.
     *
     * @param block block value consumed by the API.
     * @return Result produced by `withDownload`.
     * @author Dmitriy Moroz
     */
    override suspend fun <T> withDownload(block: suspend () -> T): T {
        wakeLockInterActor.acquireWakelockUseCase()
        try {
            return block()
        } finally {
            wakeLockInterActor.releaseWakeLockUseCase()
        }
    }
}
