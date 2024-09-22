package com.shifthackz.aisdv1.presentation.activity

import com.shifthackz.aisdv1.presentation.navigation.NavigationRoute
import com.shifthackz.android.core.mvi.MviIntent

sealed interface AppIntent : MviIntent {

    data object GrantStoragePermission : AppIntent
    data object HideSplash : AppIntent

    data class HomeRoute(val navRoute: NavigationRoute) : AppIntent
}
