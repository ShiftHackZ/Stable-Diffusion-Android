package com.shifthackz.aisdv1.presentation.navigation.router

/**
 * Defines the `WebUiRouter` contract for the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
interface WebUiRouter {
    /**
     * Executes the `navigateBack` step in the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    fun navigateBack()
}

/**
 * Provides the `NoOpWebUiRouter` singleton used by the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
object NoOpWebUiRouter : WebUiRouter {
    /**
     * Executes the `navigateBack` step in the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override fun navigateBack() = Unit
}
