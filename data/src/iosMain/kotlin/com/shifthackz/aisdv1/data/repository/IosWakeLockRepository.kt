package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.domain.repository.WakeLock
import com.shifthackz.aisdv1.domain.repository.WakeLockRepository
import platform.UIKit.UIApplication

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
        UIApplication.sharedApplication.idleTimerDisabled = true
    }

    override fun release() {
        UIApplication.sharedApplication.idleTimerDisabled = false
    }
}
