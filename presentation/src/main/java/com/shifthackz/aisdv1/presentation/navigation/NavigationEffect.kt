package com.shifthackz.aisdv1.presentation.navigation

import androidx.navigation.NavOptionsBuilder
import com.shifthackz.android.core.mvi.MviEffect

sealed interface NavigationEffect : MviEffect {

    data object Back : NavigationEffect

    sealed interface Navigate : NavigationEffect {

        val route: String

        data class Route(override val route: String) : Navigate

        data class RouteBuilder(
            override val route: String,
            val builder: NavOptionsBuilder.() -> Unit,
        ) : Navigate

        data class RoutePopUp(override val route: String) : Navigate
    }

    sealed interface Drawer : NavigationEffect {

        data object Open : Drawer

        data object Close : Drawer
    }

    sealed interface Home : NavigationEffect {
        val route: String

        data class Route(override val route: String) : Home

        data class Update(override val route: String) : Home
    }
}
