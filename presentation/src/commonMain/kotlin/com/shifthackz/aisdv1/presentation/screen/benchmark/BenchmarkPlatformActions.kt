package com.shifthackz.aisdv1.presentation.screen.benchmark

/**
 * Defines platform side effects used by the benchmark screen.
 *
 * @author Dmitriy Moroz
 */
interface BenchmarkPlatformActions {
    /**
     * Shares text with the native platform share sheet.
     *
     * @param text text payload to share.
     * @return result of the platform action.
     * @author Dmitriy Moroz
     */
    suspend fun shareText(text: String): BenchmarkActionResult
}

/**
 * Result returned by benchmark platform actions.
 *
 * @author Dmitriy Moroz
 */
sealed interface BenchmarkActionResult {
    /**
     * Indicates that the action was completed or handed to the platform.
     *
     * @author Dmitriy Moroz
     */
    data object Done : BenchmarkActionResult

    /**
     * Indicates that the current platform cannot perform the action.
     *
     * @author Dmitriy Moroz
     */
    data object Unsupported : BenchmarkActionResult

    /**
     * Indicates that the platform action failed.
     *
     * @param message user-facing failure message.
     * @author Dmitriy Moroz
     */
    data class Failed(val message: String) : BenchmarkActionResult
}

/**
 * No-op fallback for previews and tests.
 *
 * @author Dmitriy Moroz
 */
object NoOpBenchmarkPlatformActions : BenchmarkPlatformActions {
    override suspend fun shareText(text: String): BenchmarkActionResult =
        BenchmarkActionResult.Unsupported
}

/**
 * Creates default platform actions for the current target.
 *
 * @return platform-specific benchmark actions.
 * @author Dmitriy Moroz
 */
expect fun createDefaultBenchmarkPlatformActions(): BenchmarkPlatformActions
