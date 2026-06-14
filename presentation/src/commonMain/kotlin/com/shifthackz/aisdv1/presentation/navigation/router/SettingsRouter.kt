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
     * Navigates back from a standalone Settings child screen.
     *
     * @author Dmitriy Moroz
     */
    fun navigateBack()
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
    fun navigateToStorageUsage()

    fun navigateToNetworkUsage()
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
     * Navigates back from a standalone Settings child screen.
     *
     * @author Dmitriy Moroz
     */
    override fun navigateBack() = Unit
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
    override fun navigateToStorageUsage() = Unit

    override fun navigateToNetworkUsage() = Unit
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
