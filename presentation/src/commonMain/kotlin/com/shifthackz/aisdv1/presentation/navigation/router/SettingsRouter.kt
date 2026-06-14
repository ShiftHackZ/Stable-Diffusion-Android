package com.shifthackz.aisdv1.presentation.navigation.router

import com.shifthackz.aisdv1.presentation.model.LaunchSource

/**
 * Defines the `SettingsRouter` contract for the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
interface SettingsRouter {
    /**
     * Executes the `openDrawer` step in the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    fun openDrawer()
    /**
     * Executes the `closeDrawer` step in the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    fun closeDrawer()
    /**
     * Executes the `navigateToServerSetup` step in the SDAI presentation layer.
     *
     * @param source source value consumed by the API.
     * @author Dmitriy Moroz
     */
    fun navigateToServerSetup(source: LaunchSource)
    /**
     * Opens the hardware benchmark screen.
     *
     * @author Dmitriy Moroz
     */
    fun navigateToBenchmark()
    /**
     * Executes the `navigateToDebugMenu` step in the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    fun navigateToDebugMenu()
    /**
     * Executes the `navigateToDonate` step in the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    fun navigateToDonate()
    /**
     * Executes the `navigateToOnBoarding` step in the SDAI presentation layer.
     *
     * @param source source value consumed by the API.
     * @author Dmitriy Moroz
     */
    fun navigateToOnBoarding(source: LaunchSource)
}

/**
 * Provides the `NoOpSettingsRouter` singleton used by the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
object NoOpSettingsRouter : SettingsRouter {
    /**
     * Executes the `openDrawer` step in the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override fun openDrawer() = Unit
    /**
     * Executes the `closeDrawer` step in the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override fun closeDrawer() = Unit
    /**
     * Executes the `navigateToServerSetup` step in the SDAI presentation layer.
     *
     * @param source source value consumed by the API.
     * @author Dmitriy Moroz
     */
    override fun navigateToServerSetup(source: LaunchSource) = Unit
    /**
     * Opens the hardware benchmark screen.
     *
     * @author Dmitriy Moroz
     */
    override fun navigateToBenchmark() = Unit
    /**
     * Executes the `navigateToDebugMenu` step in the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override fun navigateToDebugMenu() = Unit
    /**
     * Executes the `navigateToDonate` step in the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override fun navigateToDonate() = Unit
    /**
     * Executes the `navigateToOnBoarding` step in the SDAI presentation layer.
     *
     * @param source source value consumed by the API.
     * @author Dmitriy Moroz
     */
    override fun navigateToOnBoarding(source: LaunchSource) = Unit
}
