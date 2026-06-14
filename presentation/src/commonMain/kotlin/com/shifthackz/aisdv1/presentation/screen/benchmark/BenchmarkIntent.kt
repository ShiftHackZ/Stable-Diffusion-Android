package com.shifthackz.aisdv1.presentation.screen.benchmark

import com.shifthackz.aisdv1.core.mvi.MviIntent

/**
 * Describes user actions handled by [BenchmarkViewModel].
 *
 * @author Dmitriy Moroz
 */
sealed interface BenchmarkIntent : MviIntent {
    /**
     * Closes the benchmark screen.
     *
     * @author Dmitriy Moroz
     */
    data object NavigateBack : BenchmarkIntent

    /**
     * Runs a safe synthetic benchmark and stores the result.
     *
     * @author Dmitriy Moroz
     */
    data object RunBenchmark : BenchmarkIntent

    /**
     * Shares the latest benchmark result as text.
     *
     * @author Dmitriy Moroz
     */
    data object ShareResults : BenchmarkIntent

    /**
     * Hides the current benchmark error message.
     *
     * @author Dmitriy Moroz
     */
    data object DismissError : BenchmarkIntent
}
