package com.shifthackz.aisdv1.domain.usecase.wakelock

interface ReleaseWakeLockUseCase {
    operator fun invoke(): Result<Unit>
}
