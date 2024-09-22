package com.shifthackz.aisdv1.presentation.navigation.router.home

import com.shifthackz.aisdv1.presentation.navigation.NavigationEffect
import com.shifthackz.aisdv1.presentation.navigation.NavigationRoute
import com.shifthackz.aisdv1.presentation.navigation.router.Router

interface HomeRouter : Router<NavigationEffect.Home> {

    fun updateExternallyWithoutNavigation(navRoute: NavigationRoute)

    fun navigateToRoute(navRoute: NavigationRoute)

    fun navigateToTxt2Img()

    fun navigateToImg2Img()

    fun navigateToGallery()

    fun navigateToSettings()
}
