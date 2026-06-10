package com.shifthackz.aisdv1.domain.interactor.wakelock

import com.shifthackz.aisdv1.domain.usecase.wakelock.AcquireWakelockUseCase
import com.shifthackz.aisdv1.domain.usecase.wakelock.ReleaseWakeLockUseCase

interface WakeLockInterActor {
    val acquireWakelockUseCase: AcquireWakelockUseCase
    val releaseWakeLockUseCase: ReleaseWakeLockUseCase
}
