package com.shifthackz.aisdv1.presentation.navigation.router

/**
 * Defines the `DebugMenuRouter` contract for the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
interface DebugMenuRouter {
    /**
     * Executes the `navigateBack` step in the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    fun navigateBack()
    /**
     * Executes the `navigateToLogger` step in the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    fun navigateToLogger()
}

/**
 * Provides the `NoOpDebugMenuRouter` singleton used by the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
object NoOpDebugMenuRouter : DebugMenuRouter {
    /**
     * Executes the `navigateBack` step in the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override fun navigateBack() = Unit
    /**
     * Executes the `navigateToLogger` step in the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override fun navigateToLogger() = Unit
}
