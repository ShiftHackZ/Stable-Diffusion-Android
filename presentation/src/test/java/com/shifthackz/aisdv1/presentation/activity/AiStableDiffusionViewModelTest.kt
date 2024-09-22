@file:OptIn(ExperimentalCoroutinesApi::class)

package com.shifthackz.aisdv1.presentation.activity

import androidx.navigation.NavOptionsBuilder
import app.cash.turbine.test
import com.shifthackz.aisdv1.domain.entity.Settings
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.presentation.core.CoreViewModelInitializeStrategy
import com.shifthackz.aisdv1.presentation.core.CoreViewModelTest
import com.shifthackz.aisdv1.presentation.navigation.NavigationEffect
import com.shifthackz.aisdv1.presentation.navigation.NavigationRoute
import com.shifthackz.aisdv1.presentation.navigation.router.drawer.DrawerRouter
import com.shifthackz.aisdv1.presentation.navigation.router.home.HomeRouter
import com.shifthackz.aisdv1.presentation.navigation.router.main.MainRouter
import com.shifthackz.aisdv1.presentation.stub.stubDispatchersProvider
import com.shifthackz.aisdv1.presentation.stub.stubSchedulersProvider
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.test.runTest
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
        dispatchersProvider = stubDispatchersProvider,
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
    fun `given received GrantStoragePermission intent, expected VM sets field saveToMediaStore with true in preference manager`() {
        every {
            stubPreferenceManager::saveToMediaStore.set(any())
        } returns Unit

        viewModel.processIntent(AppIntent.GrantStoragePermission)

        verify {
            stubPreferenceManager::saveToMediaStore.set(true)
        }
    }

    @Test
    fun `given received HideSplash intent, expected VM sets isShowSplash to false in UI state`() {
        with(viewModel) {
            processIntent(AppIntent.HideSplash)
            Assert.assertFalse(state.value.isShowSplash)
        }
    }

    @Test
    fun `given route event from main router, expected domain model delivered to effect collector`() {
        stubNavigationEffect.onNext(NavigationEffect.Navigate.Route(navRoute = NavigationRoute.Splash))
        runTest {
            val expected = NavigationEffect.Navigate.Route(navRoute = NavigationRoute.Splash)
            val actual = viewModel.effect.firstOrNull()
            Assert.assertEquals(expected, actual)
        }
    }

    @Test
    fun `given route pop up event from main router, expected domain model delivered to effect collector`() {
        stubNavigationEffect.onNext(NavigationEffect.Navigate.RoutePopUp(navRoute = NavigationRoute.Splash))
        runTest {
            val expected = NavigationEffect.Navigate.RoutePopUp(navRoute = NavigationRoute.Splash)
            val actual = viewModel.effect.firstOrNull()
            Assert.assertEquals(expected, actual)
        }
    }

    @Test
    fun `given route builder event from main router, expected domain model delivered to effect collector`() {
        stubNavigationEffect.onNext(
            NavigationEffect.Navigate.RouteBuilder(
                navRoute = NavigationRoute.Splash,
                stubNavBuilder
            )
        )
        runTest {
            val expected = NavigationEffect.Navigate.RouteBuilder(
                navRoute = NavigationRoute.Splash,
                stubNavBuilder
            )
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
        runTest {
            viewModel.effect.test {
                stubNavigationEffect.onNext(NavigationEffect.Navigate.Route(navRoute = NavigationRoute.Splash))
                Assert.assertEquals(
                    NavigationEffect.Navigate.Route(navRoute = NavigationRoute.Splash),
                    awaitItem()
                )

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
                stubNavigationEffect.onNext(NavigationEffect.Navigate.Route(navRoute = NavigationRoute.Splash))
                Assert.assertEquals(
                    NavigationEffect.Navigate.Route(navRoute = NavigationRoute.Splash),
                    awaitItem()
                )

                stubNavigationEffect.onNext(NavigationEffect.Navigate.Route(navRoute = NavigationRoute.InPaint))
                Assert.assertEquals(
                    NavigationEffect.Navigate.Route(navRoute = NavigationRoute.InPaint),
                    awaitItem()
                )

                stubNavigationEffect.onNext(NavigationEffect.Back)
                Assert.assertEquals(NavigationEffect.Back, awaitItem())

                stubNavigationEffect.onNext(NavigationEffect.Navigate.Route(navRoute = NavigationRoute.Donate))
                Assert.assertEquals(NavigationEffect.Navigate.Route(navRoute = NavigationRoute.Donate), awaitItem())

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
