package com.shifthackz.aisdv1.presentation.navigation

import com.shifthackz.aisdv1.presentation.model.LaunchSource
import kotlinx.serialization.Serializable

sealed interface NavigationRoute {
    @Serializable
    data object Splash : NavigationRoute

    @Serializable
    data class Onboarding(val source: LaunchSource) : NavigationRoute
}
