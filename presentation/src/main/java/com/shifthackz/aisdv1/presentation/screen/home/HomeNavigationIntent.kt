package com.shifthackz.aisdv1.presentation.screen.home

import com.shifthackz.aisdv1.presentation.navigation.NavigationRoute
import com.shifthackz.android.core.mvi.MviIntent

sealed interface HomeNavigationIntent : MviIntent {

    data class Route(val navRoute: NavigationRoute) : HomeNavigationIntent

    data class Update(val navRoute: NavigationRoute) : HomeNavigationIntent
}
