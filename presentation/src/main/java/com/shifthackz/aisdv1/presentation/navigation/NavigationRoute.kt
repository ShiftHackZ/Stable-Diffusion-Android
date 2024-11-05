package com.shifthackz.aisdv1.presentation.navigation

import com.shifthackz.aisdv1.presentation.model.LaunchSource
import kotlinx.serialization.Serializable

sealed interface NavigationRoute {
    @Serializable
    data object Splash : NavigationRoute

    @Serializable
    data object Home : NavigationRoute

    @Serializable
    data object ConfigLoader : NavigationRoute

    @Serializable
    data object WebUi : NavigationRoute

    @Serializable
    data object Debug : NavigationRoute

    @Serializable
    data object Logger : NavigationRoute

    @Serializable
    data object InPaint : NavigationRoute

    @Serializable
    data object Donate : NavigationRoute

    @Serializable
    data class Onboarding(val source: LaunchSource) : NavigationRoute

    @Serializable
    data class GalleryDetail(val itemId: Long) : NavigationRoute

    @Serializable
    data class ReportImage(val itemId: Long) : NavigationRoute

    @Serializable
    data class ServerSetup(val source: LaunchSource) : NavigationRoute

    sealed interface HomeNavigation : NavigationRoute {
        @Serializable
        data object TxtToImg : HomeNavigation

        @Serializable
        data object ImgToImg : HomeNavigation

        @Serializable
        data object Gallery : HomeNavigation

        @Serializable
        data object Settings : HomeNavigation
    }
}
