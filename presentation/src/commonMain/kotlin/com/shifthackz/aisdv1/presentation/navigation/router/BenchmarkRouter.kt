package com.shifthackz.aisdv1.presentation.navigation.router

/**
 * Defines navigation actions available from the benchmark screen.
 *
 * @author Dmitriy Moroz
 */
interface BenchmarkRouter {
    /**
     * Returns to the previous screen.
     *
     * @author Dmitriy Moroz
     */
    fun navigateBack()
}

/**
 * No-op benchmark router used when the screen is rendered in isolation.
 *
 * @author Dmitriy Moroz
 */
object NoOpBenchmarkRouter : BenchmarkRouter {
    /**
     * Returns to the previous screen.
     *
     * @author Dmitriy Moroz
     */
    override fun navigateBack() = Unit
}
