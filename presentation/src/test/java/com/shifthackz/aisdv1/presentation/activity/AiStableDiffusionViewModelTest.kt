@file:OptIn(ExperimentalCoroutinesApi::class)

package com.shifthackz.aisdv1.presentation.activity

import androidx.navigation.NavOptionsBuilder
import app.cash.turbine.test
import com.shifthackz.aisdv1.domain.entity.Settings
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.presentation.core.CoreViewModelInitializeStrategy
import com.shifthackz.aisdv1.presentation.core.CoreViewModelTest
import com.shifthackz.aisdv1.presentation.navigation.NavigationEffect
import com.shifthackz.aisdv1.presentation.navigation.router.drawer.DrawerRouter
import com.shifthackz.aisdv1.presentation.navigation.router.home.HomeRouter
import com.shifthackz.aisdv1.presentation.navigation.router.main.MainRouter
import com.shifthackz.aisdv1.presentation.stub.stubSchedulersProvider
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class AiStableDiffusionViewModelTest : CoreViewModelTest<AiStableDiffusionViewModel>() {

    private val stubNavigationEffect = BehaviorSubject.create<NavigationEffect>()
    private val stubDrawerNavigationEffect = BehaviorSubject.create<NavigationEffect.Drawer>()
    private val stubHomeNavigationEffect = BehaviorSubject.create<NavigationEffect.Home>()
    private val stubNavBuilder: NavOptionsBuilder.() -> Unit = {
        popUpTo("splash") { inclusive = true }
    }

    private val stubMainRouter = mockk<MainRouter>()
    private val stubDrawerRouter = mockk<DrawerRouter>()
    private val stubHomeRouter = mockk<HomeRouter>()
    private val stubPreferenceManager = mockk<PreferenceManager>()

    override val testViewModelStrategy = CoreViewModelInitializeStrategy.InitializeEveryTime

    override fun initializeViewModel() = AiStableDiffusionViewModel(
        schedulersProvider = stubSchedulersProvider,
        mainRouter = stubMainRouter,
        drawerRouter = stubDrawerRouter,
        homeRouter = stubHomeRouter,
        preferenceManager = stubPreferenceManager,
    )

    @Before
    override fun initialize() {
        super.initialize()

        every {
            stubMainRouter.observe()
        } returns stubNavigationEffect

        every {
            stubDrawerRouter.observe()
        } returns stubDrawerNavigationEffect

        every {
            stubHomeRouter.observe()
        } returns stubHomeNavigationEffect

        every {
            stubPreferenceManager.observe()
        } returns Flowable.just(Settings())
    }

    @Test
    fun `given onStoragePermissionsGranted was called, expected VM sets field saveToMediaStore with true in preference manager`() {
        every {
            stubPreferenceManager::saveToMediaStore.set(any())
        } returns Unit

        viewModel.processIntent(AppIntent.GrantStoragePermission)

        verify {
            stubPreferenceManager::saveToMediaStore.set(true)
        }
    }

    @Test
    fun `given route event from main router, expected domain model delivered to effect collector`() {
        stubNavigationEffect.onNext(NavigationEffect.Navigate.Route("route"))
        runTest {
            val expected = NavigationEffect.Navigate.Route("route")
            val actual = viewModel.effect.firstOrNull()
            Assert.assertEquals(expected, actual)
        }
    }

    @Test
    fun `given route pop up event from main router, expected domain model delivered to effect collector`() {
        stubNavigationEffect.onNext(NavigationEffect.Navigate.RoutePopUp("route"))
        runTest {
            val expected = NavigationEffect.Navigate.RoutePopUp("route")
            val actual = viewModel.effect.firstOrNull()
            Assert.assertEquals(expected, actual)
        }
    }

    @Test
    fun `given route builder event from main router, expected domain model delivered to effect collector`() {
        stubNavigationEffect.onNext(
            NavigationEffect.Navigate.RouteBuilder("route", stubNavBuilder)
        )
        runTest {
            val expected = NavigationEffect.Navigate.RouteBuilder("route", stubNavBuilder)
            val actual = viewModel.effect.firstOrNull()
            Assert.assertEquals(expected, actual)
        }
    }

    @Test
    fun `given back event from main router, expected domain model delivered to effect collector`() {
        stubNavigationEffect.onNext(NavigationEffect.Back)
        runTest {
            val expected = NavigationEffect.Back
            val actual = viewModel.effect.firstOrNull()
            Assert.assertEquals(expected, actual)
        }
    }

    @Test
    fun `given route then back events from main router, expected two domain models delivered to effect collector in same order`() {
        Dispatchers.setMain(StandardTestDispatcher())
        runTest {
            viewModel.effect.test {
                stubNavigationEffect.onNext(NavigationEffect.Navigate.Route("route2"))
                Assert.assertEquals(NavigationEffect.Navigate.Route("route2"), awaitItem())

                stubNavigationEffect.onNext(NavigationEffect.Back)
                Assert.assertEquals(NavigationEffect.Back, awaitItem())

                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Test
    fun `given mixed six events from main router, expected six domain models delivered to effect collector in same order`() {
        runTest {
            viewModel.effect.test {
                stubNavigationEffect.onNext(NavigationEffect.Navigate.Route("route2"))
                Assert.assertEquals(NavigationEffect.Navigate.Route("route2"), awaitItem())

                stubNavigationEffect.onNext(NavigationEffect.Navigate.Route("route4"))
                Assert.assertEquals(NavigationEffect.Navigate.Route("route4"), awaitItem())

                stubNavigationEffect.onNext(NavigationEffect.Back)
                Assert.assertEquals(NavigationEffect.Back, awaitItem())

                stubNavigationEffect.onNext(NavigationEffect.Navigate.Route("route3"))
                Assert.assertEquals(NavigationEffect.Navigate.Route("route3"), awaitItem())

                stubNavigationEffect.onNext(NavigationEffect.Back)
                Assert.assertEquals(NavigationEffect.Back, awaitItem())

                stubNavigationEffect.onNext(NavigationEffect.Back)
                Assert.assertEquals(NavigationEffect.Back, awaitItem())

                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Test
    fun `given open then close events from drawer router, expected two domain models delivered to effect collector in same order`() {
        runTest {
            viewModel.effect.test {
                stubDrawerNavigationEffect.onNext(NavigationEffect.Drawer.Open)
                Assert.assertEquals(NavigationEffect.Drawer.Open, awaitItem())

                stubDrawerNavigationEffect.onNext(NavigationEffect.Drawer.Close)
                Assert.assertEquals(NavigationEffect.Drawer.Close, awaitItem())

                cancelAndIgnoreRemainingEvents()
            }
        }
    }
}
