package com.shifthackz.aisdv1.presentation.navigation.router.drawer

import com.shifthackz.aisdv1.presentation.navigation.NavigationEffect
import com.shifthackz.aisdv1.presentation.navigation.router.Router

interface DrawerRouter : Router<NavigationEffect.Drawer> {

    fun openDrawer()

    fun closeDrawer()
}
