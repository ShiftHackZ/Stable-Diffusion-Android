package com.shifthackz.aisdv1.presentation.navigation.router.main

import com.shifthackz.aisdv1.presentation.model.LaunchSource
import com.shifthackz.aisdv1.presentation.navigation.NavigationEffect
import com.shifthackz.aisdv1.presentation.navigation.router.Router

interface MainRouter : Router<NavigationEffect> {

    fun navigateBack()

    fun navigateToOnBoarding(source: LaunchSource)

    fun navigateToPostSplashConfigLoader()

    fun navigateToHomeScreen()

    fun navigateToServerSetup(source: LaunchSource)

    fun navigateToGalleryDetails(itemId: Long)

    fun navigateToReportImage(itemId: Long)

    fun navigateToInPaint()

    fun navigateToDonate()

    fun navigateToDebugMenu()

    fun navigateToLogger()
}
