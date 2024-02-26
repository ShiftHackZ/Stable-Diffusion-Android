package com.shifthackz.aisdv1.domain.repository

import android.os.PowerManager

interface WakeLockRepository {
    val wakeLock: PowerManager.WakeLock
}
