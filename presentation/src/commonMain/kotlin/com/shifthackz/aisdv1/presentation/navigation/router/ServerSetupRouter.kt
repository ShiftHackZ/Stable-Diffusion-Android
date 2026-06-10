package com.shifthackz.aisdv1.presentation.navigation.router

/**
 * Defines the `ServerSetupRouter` contract for the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
interface ServerSetupRouter {
    /**
     * Executes the `navigateBack` step in the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    fun navigateBack()
    /**
     * Executes the `navigateToPostSetupConfigLoader` step in the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    fun navigateToPostSetupConfigLoader()
}

/**
 * Provides the `NoOpServerSetupRouter` singleton used by the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
object NoOpServerSetupRouter : ServerSetupRouter {
    /**
     * Executes the `navigateBack` step in the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override fun navigateBack() = Unit
    /**
     * Executes the `navigateToPostSetupConfigLoader` step in the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override fun navigateToPostSetupConfigLoader() = Unit
}
