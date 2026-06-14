package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.domain.repository.WakeLock
import com.shifthackz.aisdv1.domain.repository.WakeLockRepository
import platform.Foundation.NSThread
import platform.UIKit.UIApplication
import platform.darwin.dispatch_get_main_queue
import platform.darwin.dispatch_sync

/**
 * Implements `WakeLockRepository` behavior for iOS.
 *
 * @author Dmitriy Moroz
 */
internal class IosWakeLockRepository : WakeLockRepository {
    /**
     * Exposes the `wakeLock` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    override val wakeLock: WakeLock = IosWakeLock
}

private object IosWakeLock : WakeLock {
    override fun acquire(timeout: Long) {
        runOnMainThread {
            UIApplication.sharedApplication.idleTimerDisabled = true
        }
    }

    override fun release() {
        runOnMainThread {
            UIApplication.sharedApplication.idleTimerDisabled = false
        }
    }

    private inline fun runOnMainThread(crossinline block: () -> Unit) {
        if (NSThread.isMainThread) {
            block()
        } else {
            dispatch_sync(dispatch_get_main_queue()) {
                block()
            }
        }
    }
}
