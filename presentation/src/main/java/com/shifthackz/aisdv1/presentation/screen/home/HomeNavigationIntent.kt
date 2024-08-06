package com.shifthackz.aisdv1.presentation.screen.home

import com.shifthackz.android.core.mvi.MviIntent

sealed interface HomeNavigationIntent : MviIntent {

    data class Route(val route: String) : HomeNavigationIntent

    data class Update(val route: String) : HomeNavigationIntent
}
