package com.shifthackz.aisdv1.presentation.navigation

import com.shifthackz.aisdv1.presentation.screen.setup.ServerSetupLaunchSource

interface Router {

    fun navigateBack()

    fun navigateToPostSplashConfigLoader()

    fun navigateToHomeScreen()

    fun navigateToServerSetup(source: ServerSetupLaunchSource)

    fun navigateToGalleryDetails(itemId: Long)

    fun navigateToDebugMenu()
}
