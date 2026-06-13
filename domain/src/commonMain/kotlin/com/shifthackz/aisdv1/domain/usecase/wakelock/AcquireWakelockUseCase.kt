package com.shifthackz.aisdv1.domain.usecase.wakelock

/**
 * Defines the `AcquireWakelockUseCase` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
interface AcquireWakelockUseCase {
    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @param timeout timeout value consumed by the API.
     * @return Result produced by `invoke`.
     * @author Dmitriy Moroz
     */
    operator fun invoke(timeout: Long = DEFAULT_TIMEOUT): Result<Unit>

    /**
     * Provides the `companion object` singleton used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    companion object {
        /**
         * Exposes the `DEFAULT_TIMEOUT` value used by the SDAI domain layer.
         *
         * @author Dmitriy Moroz
         */
        const val DEFAULT_TIMEOUT = 10 * 60 * 1000L
    }
}
