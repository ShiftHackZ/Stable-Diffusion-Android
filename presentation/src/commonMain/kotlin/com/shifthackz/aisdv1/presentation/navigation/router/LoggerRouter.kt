package com.shifthackz.aisdv1.presentation.navigation.router

/**
 * Defines the `LoggerRouter` contract for the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
interface LoggerRouter {
    /**
     * Executes the `navigateBack` step in the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    fun navigateBack()
}

/**
 * Provides the `NoOpLoggerRouter` singleton used by the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
object NoOpLoggerRouter : LoggerRouter {
    /**
     * Executes the `navigateBack` step in the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override fun navigateBack() = Unit
}
