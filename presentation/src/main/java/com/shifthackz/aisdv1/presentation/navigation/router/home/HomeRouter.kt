package com.shifthackz.aisdv1.presentation.navigation.router.home

import com.shifthackz.aisdv1.presentation.navigation.NavigationEffect
import com.shifthackz.aisdv1.presentation.navigation.router.Router

interface HomeRouter : Router<NavigationEffect.Home> {

    fun updateExternallyWithoutNavigation(route: String)

    fun navigateToRoute(route: String)

    fun navigateToTxt2Img()

    fun navigateToImg2Img()

    fun navigateToGallery()

    fun navigateToSettings()
}
