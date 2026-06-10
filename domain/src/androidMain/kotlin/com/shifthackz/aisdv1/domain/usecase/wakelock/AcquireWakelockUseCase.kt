package com.shifthackz.aisdv1.domain.usecase.wakelock

interface AcquireWakelockUseCase {
    operator fun invoke(timeout: Long = DEFAULT_TIMEOUT): Result<Unit>

    companion object {
        const val DEFAULT_TIMEOUT = 10 * 60 * 1000L // 60 minutes
    }
}
