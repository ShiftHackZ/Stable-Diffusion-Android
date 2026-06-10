package com.shifthackz.aisdv1.presentation.navigation.router

import com.shifthackz.aisdv1.presentation.model.LaunchSource

interface SettingsRouter {
    fun openDrawer()
    fun closeDrawer()
    fun navigateToServerSetup(source: LaunchSource)
    fun navigateToDebugMenu()
    fun navigateToDonate()
    fun navigateToOnBoarding(source: LaunchSource)
}

object NoOpSettingsRouter : SettingsRouter {
    override fun openDrawer() = Unit
    override fun closeDrawer() = Unit
    override fun navigateToServerSetup(source: LaunchSource) = Unit
    override fun navigateToDebugMenu() = Unit
    override fun navigateToDonate() = Unit
    override fun navigateToOnBoarding(source: LaunchSource) = Unit
}
