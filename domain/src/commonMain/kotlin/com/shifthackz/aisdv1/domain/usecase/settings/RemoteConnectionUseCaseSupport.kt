package com.shifthackz.aisdv1.domain.usecase.settings

import com.shifthackz.aisdv1.domain.entity.Configuration
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay

internal suspend fun restoreRemoteConfigurationAndFail(
    configuration: Configuration?,
    setServerConfigurationUseCase: SetServerConfigurationUseCase,
    throwable: Throwable,
): Result<Unit> {
    if (throwable is CancellationException && throwable !is TimeoutCancellationException) {
        throw throwable
    }
    configuration?.let { setServerConfigurationUseCase(it) }
    return Result.failure(throwable)
}

internal fun requireRemoteValidApiKey(valid: Boolean) {
    if (!valid) throw IllegalStateException("Bad key")
}

internal suspend fun retryRemoteDelayed(
    attempts: Int,
    delayMillis: Long,
    block: suspend () -> Unit,
) {
    var lastThrowable: Throwable? = null
    repeat(attempts) { attempt ->
        try {
            block()
            return
        } catch (t: Throwable) {
            if (t is CancellationException) throw t
            lastThrowable = t
            if (attempt < attempts - 1) delay(delayMillis)
        }
    }
    throw lastThrowable ?: IllegalStateException("Retry failed without an exception.")
}
