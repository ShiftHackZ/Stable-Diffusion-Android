package com.shifthackz.aisdv1.presentation.navigation.router.main

import com.shifthackz.aisdv1.presentation.model.LaunchSource
import com.shifthackz.aisdv1.presentation.navigation.NavigationEffect
import com.shifthackz.aisdv1.presentation.navigation.NavigationRoute
import org.junit.Test

class MainRouterImplTest {

    private val router = MainRouterImpl()

    @Test
    fun `given user navigates back, expected router emits Back event`() {
        router
            .observe()
            .test()
            .also { router.navigateBack() }
            .assertNoErrors()
            .assertValueAt(0, NavigationEffect.Back)
    }

    @Test
    fun `given user navigates to splash config loader, expected router emits RouteBuilder event with ConfigLoader route`() {
        router
            .observe()
            .test()
            .also { router.navigateToPostSplashConfigLoader() }
            .assertNoErrors()
            .assertValueAt(0) { actual ->
                actual is NavigationEffect.Navigate.RouteBuilder
                        && actual.navRoute == NavigationRoute.ConfigLoader
            }
    }

    @Test
    fun `given user navigates to home screen, expected router emits RoutePopUp event with Home route`() {
        router
            .observe()
            .test()
            .also { router.navigateToHomeScreen() }
            .assertNoErrors()
            .assertValueAt(0) { actual ->
                val expectedRoute = NavigationRoute.Home
                actual is NavigationEffect.Navigate.RouteBuilder
                        && actual.navRoute == expectedRoute
            }
    }

    @Test
    fun `given user navigates to server setup from splash, expected router emits RouteBuilder event with ServerSetup route and SPLASH source`() {
        router
            .observe()
            .test()
            .also { router.navigateToServerSetup(LaunchSource.SPLASH) }
            .assertNoErrors()
            .assertValueAt(0) { actual ->
                val expectedRoute = NavigationRoute.ServerSetup(source = LaunchSource.SPLASH)
                actual is NavigationEffect.Navigate.RouteBuilder
                        && actual.navRoute == expectedRoute
            }
    }

    @Test
    fun `given user navigates to server setup from settings, expected router emits RouteBuilder event with ServerSetup route and SETTINGS source`() {
        router
            .observe()
            .test()
            .also { router.navigateToServerSetup(LaunchSource.SETTINGS) }
            .assertNoErrors()
            .assertValueAt(0) { actual ->
                val expectedRoute = NavigationRoute.ServerSetup(source = LaunchSource.SETTINGS)
                actual is NavigationEffect.Navigate.RouteBuilder
                        && actual.navRoute == expectedRoute
            }
    }

    @Test
    fun `given user navigates to gallery details for item 5598, expected router emits Route event with GalleryDetail route and id 5598`() {
        router
            .observe()
            .test()
            .also { router.navigateToGalleryDetails(5598L) }
            .assertNoErrors()
            .assertValueAt(
                0,
                NavigationEffect.Navigate.Route(NavigationRoute.GalleryDetail(itemId = 5598L)),
            )
    }

    @Test
    fun `given user navigates to in paint, expected router emits Route event with InPaint route`() {
        router
            .observe()
            .test()
            .also { router.navigateToInPaint() }
            .assertNoErrors()
            .assertValueAt(
                0,
                NavigationEffect.Navigate.Route(navRoute = NavigationRoute.InPaint),
            )
    }

    @Test
    fun `given user tapped hidden menu 7 times, build is debuggable, expected router emits Route event with Debug route`() {
        val stubObserver = router.observe().test()

        router.navigateToDebugMenu()

        stubObserver
            .assertNoErrors()
            .assertValueAt(0, NavigationEffect.Navigate.Route(navRoute = NavigationRoute.Debug))
    }
}
