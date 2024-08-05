package com.shifthackz.aisdv1.presentation.navigation.router.drawer

import com.shifthackz.aisdv1.presentation.navigation.NavigationEffect
import org.junit.Test

class DrawerRouterImplTest {

    private val router = DrawerRouterImpl()

    @Test
    fun `given user opens drawer, expected router emits Open event`() {
        router
            .observe()
            .test()
            .also { router.openDrawer() }
            .assertNoErrors()
            .assertValueAt(0, NavigationEffect.Drawer.Open)
    }

    @Test
    fun `given user closes drawer, expected router emits Close event`() {
        router
            .observe()
            .test()
            .also { router.closeDrawer() }
            .assertNoErrors()
            .assertValueAt(0, NavigationEffect.Drawer.Close)
    }

    @Test
    fun `given user opens than closes drawer, expected router emits Open than Close events`() {
        router
            .observe()
            .test()
            .also { router.openDrawer() }
            .assertNoErrors()
            .assertValueAt(0, NavigationEffect.Drawer.Open)
            .also { router.closeDrawer() }
            .assertNoErrors()
            .assertValueAt(1, NavigationEffect.Drawer.Close)
    }
}
