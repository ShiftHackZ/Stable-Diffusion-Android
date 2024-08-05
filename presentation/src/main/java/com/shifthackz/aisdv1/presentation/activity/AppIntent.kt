package com.shifthackz.aisdv1.presentation.activity

import com.shifthackz.android.core.mvi.MviIntent

sealed interface AppIntent : MviIntent {

    data object GrantStoragePermission : AppIntent

    data class HomeRoute(val route: String) : AppIntent
}
