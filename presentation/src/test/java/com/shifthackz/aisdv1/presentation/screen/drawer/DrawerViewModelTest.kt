package com.shifthackz.aisdv1.presentation.screen.drawer

import com.shifthackz.aisdv1.presentation.core.CoreViewModelTest
import com.shifthackz.aisdv1.presentation.navigation.router.drawer.DrawerRouter
import com.shifthackz.aisdv1.presentation.stub.stubDispatchersProvider
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test

class DrawerViewModelTest : CoreViewModelTest<DrawerViewModel>() {

    private val stubDrawerRouter = mockk<DrawerRouter>()

    override fun initializeViewModel() = DrawerViewModel(
        dispatchersProvider = stubDispatchersProvider,
        drawerRouter = stubDrawerRouter,
    )

    @Test
    fun `given received Close intent, expected router closeDrawer() method called`() {
        every {
            stubDrawerRouter.closeDrawer()
        } returns Unit

        viewModel.processIntent(DrawerIntent.Close)

        verify {
            stubDrawerRouter.closeDrawer()
        }
    }

    @Test
    fun `given received Open intent, expected router openDrawer() method called`() {
        every {
            stubDrawerRouter.openDrawer()
        } returns Unit

        viewModel.processIntent(DrawerIntent.Open)

        verify {
            stubDrawerRouter.openDrawer()
        }
    }
}
