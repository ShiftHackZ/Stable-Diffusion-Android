package com.shifthackz.aisdv1.presentation.screen.drawer

import com.shifthackz.android.core.mvi.MviIntent

sealed interface DrawerIntent : MviIntent {

    data object Open : DrawerIntent

    data object Close : DrawerIntent
}
