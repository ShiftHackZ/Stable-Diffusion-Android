package com.shifthackz.aisdv1.presentation.screen.drawer

import com.shifthackz.aisdv1.presentation.navigation.router.drawer.DrawerRouter
import com.shifthackz.android.core.mvi.EmptyEffect
import com.shifthackz.android.core.mvi.EmptyState
import com.shifthackz.android.core.mvi.MviViewModel

class DrawerViewModel(
    private val drawerRouter: DrawerRouter,
) : MviViewModel<EmptyState, DrawerIntent, EmptyEffect>() {

    override val initialState = EmptyState

    override fun processIntent(intent: DrawerIntent) {
        when (intent) {
            DrawerIntent.Close -> drawerRouter.closeDrawer()
            DrawerIntent.Open -> drawerRouter.openDrawer()
        }
    }
}
