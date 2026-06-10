package com.shifthackz.aisdv1.domain.repository

import android.os.PowerManager

/**
 * Defines the `WakeLockRepository` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
interface WakeLockRepository {
    /**
     * Exposes the `wakeLock` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val wakeLock: PowerManager.WakeLock
}
