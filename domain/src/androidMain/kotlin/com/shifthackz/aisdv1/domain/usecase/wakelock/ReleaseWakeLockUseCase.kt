package com.shifthackz.aisdv1.domain.usecase.wakelock

/**
 * Defines the `ReleaseWakeLockUseCase` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
interface ReleaseWakeLockUseCase {
    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @return Result produced by `invoke`.
     * @author Dmitriy Moroz
     */
    operator fun invoke(): Result<Unit>
}
