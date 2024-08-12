package com.shifthackz.aisdv1.presentation.navigation.router.main

import com.shifthackz.aisdv1.presentation.navigation.NavigationEffect
import com.shifthackz.aisdv1.presentation.navigation.router.Router
import com.shifthackz.aisdv1.presentation.screen.setup.ServerSetupLaunchSource

interface MainRouter : Router<NavigationEffect> {

    fun navigateBack()

    fun navigateToPostSplashConfigLoader()

    fun navigateToHomeScreen()

    fun navigateToServerSetup(source: ServerSetupLaunchSource)

    fun navigateToGalleryDetails(itemId: Long)

    fun navigateToInPaint()

    fun navigateToDonate()

    fun navigateToDebugMenu()

    fun navigateToLogger()
}
