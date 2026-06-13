package com.shifthackz.aisdv1.presentation.screen.setup.platform

import com.shifthackz.aisdv1.domain.interactor.wakelock.WakeLockInterActor

internal class AndroidServerSetupDownloadGuard(
    private val wakeLockInterActor: WakeLockInterActor,
) : ServerSetupDownloadGuard {

    override suspend fun <T> withDownload(block: suspend () -> T): T {
        wakeLockInterActor.acquireWakelockUseCase()
        try {
            return block()
        } finally {
            wakeLockInterActor.releaseWakeLockUseCase()
        }
    }
}
