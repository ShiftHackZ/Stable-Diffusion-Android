package com.shifthackz.aisdv1.data.repository

import android.os.PowerManager
import com.shifthackz.aisdv1.domain.repository.WakeLockRepository

internal class WakeLockRepositoryImpl(
    val powerManager: () -> PowerManager,
) : WakeLockRepository {

    private var _wakeLock: PowerManager.WakeLock? = null
    override val wakeLock: PowerManager.WakeLock
        get() = _wakeLock ?: run {
            val wl = powerManager().newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG)
            _wakeLock = wl
            wl
        }

    companion object {
        private const val TAG = "SDAI:WakeLock"
    }
}
