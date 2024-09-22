package com.shifthackz.aisdv1.presentation.navigation

import androidx.navigation.NavOptionsBuilder
import com.shifthackz.android.core.mvi.MviEffect

sealed interface NavigationEffect : MviEffect {

    data object Back : NavigationEffect

    sealed interface Navigate : NavigationEffect {
        val navRoute: NavigationRoute

        data class Route(override val navRoute: NavigationRoute) : Navigate

        data class RouteBuilder(
            override val navRoute: NavigationRoute,
            val builder: NavOptionsBuilder.() -> Unit,
        ) : Navigate

        data class RoutePopUp(override val navRoute: NavigationRoute) : Navigate
    }

    sealed interface Drawer : NavigationEffect {

        data object Open : Drawer

        data object Close : Drawer
    }

    sealed interface Home : NavigationEffect {
        val navRoute: NavigationRoute

        data class Route(override val navRoute: NavigationRoute) : Home

        data class Update(override val navRoute: NavigationRoute) : Home
    }
}
