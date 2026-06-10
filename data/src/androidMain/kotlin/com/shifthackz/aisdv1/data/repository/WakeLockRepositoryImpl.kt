package com.shifthackz.aisdv1.data.repository

import android.os.PowerManager
import com.shifthackz.aisdv1.domain.repository.WakeLockRepository

/**
 * Implements `WakeLockRepository` behavior in the SDAI data layer.
 *
 * @author Dmitriy Moroz
 */
internal class WakeLockRepositoryImpl(
    /**
     * Exposes the `powerManager` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    val powerManager: () -> PowerManager,
) : WakeLockRepository {

    /**
     * Exposes the `_wakeLock` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private var _wakeLock: PowerManager.WakeLock? = null
    /**
     * Exposes the `wakeLock` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    override val wakeLock: PowerManager.WakeLock
        get() = _wakeLock ?: run {
            val wl = powerManager().newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG)
            _wakeLock = wl
            wl
        }

    /**
     * Provides the `companion object` singleton used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    companion object {
        /**
         * Exposes the `TAG` value used by the SDAI data layer.
         *
         * @author Dmitriy Moroz
         */
        private const val TAG = "SDAI:WakeLock"
    }
}
