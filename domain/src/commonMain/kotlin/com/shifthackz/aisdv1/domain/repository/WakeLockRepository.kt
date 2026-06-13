package com.shifthackz.aisdv1.domain.repository

/**
 * Defines the `WakeLock` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
interface WakeLock {
    /**
     * Executes the `acquire` step in the SDAI domain layer.
     *
     * @param timeout timeout value consumed by the API.
     * @author Dmitriy Moroz
     */
    fun acquire(timeout: Long)

    /**
     * Executes the `release` step in the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    fun release()
}

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
    val wakeLock: WakeLock
}

/**
 * Provides the `NoOpWakeLockRepository` singleton used by the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
object NoOpWakeLockRepository : WakeLockRepository {
    /**
     * Exposes the `wakeLock` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    override val wakeLock: WakeLock = NoOpWakeLock
}

private object NoOpWakeLock : WakeLock {
    override fun acquire(timeout: Long) = Unit

    override fun release() = Unit
}
