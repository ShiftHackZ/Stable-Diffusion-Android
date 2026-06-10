package com.shifthackz.aisdv1.domain.usecase.settings

import com.shifthackz.aisdv1.domain.entity.Configuration
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay

/**
 * Executes the `restoreRemoteConfigurationAndFail` step in the SDAI domain layer.
 *
 * @param configuration configuration value consumed by the API.
 * @param setServerConfigurationUseCase set server configuration use case value consumed by the API.
 * @param throwable throwable value consumed by the API.
 * @return Result produced by `restoreRemoteConfigurationAndFail`.
 * @author Dmitriy Moroz
 */
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

/**
 * Executes the `requireRemoteValidApiKey` step in the SDAI domain layer.
 *
 * @param valid valid value consumed by the API.
 * @throws IllegalStateException when the delegated operation cannot complete.
 * @author Dmitriy Moroz
 */
internal fun requireRemoteValidApiKey(valid: Boolean) {
    if (!valid) throw IllegalStateException("Bad key")
}

/**
 * Executes the `retryRemoteDelayed` step in the SDAI domain layer.
 *
 * @param attempts attempts value consumed by the API.
 * @param delayMillis delay millis value consumed by the API.
 * @param block block value consumed by the API.
 * @author Dmitriy Moroz
 */
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
