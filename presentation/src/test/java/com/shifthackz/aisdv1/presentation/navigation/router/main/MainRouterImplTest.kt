package com.shifthackz.aisdv1.presentation.navigation.router.main

import com.shifthackz.aisdv1.core.common.appbuild.BuildInfoProvider
import com.shifthackz.aisdv1.presentation.navigation.NavigationEffect
import com.shifthackz.aisdv1.presentation.screen.debug.DebugMenuAccessor
import com.shifthackz.aisdv1.presentation.screen.setup.ServerSetupLaunchSource
import com.shifthackz.aisdv1.presentation.utils.Constants
import io.mockk.every
import io.mockk.mockk
import org.junit.Test

class MainRouterImplTest {

    private val stubBuildInfoProvider = mockk<BuildInfoProvider>()
    private val stubDebugMenuAccessor = DebugMenuAccessor(stubBuildInfoProvider)

    private val router = MainRouterImpl(stubDebugMenuAccessor)

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
    fun `given user navigates to splash config loader, expected router emits RouteBuilder event with ROUTE_CONFIG_LOADER route`() {
        router
            .observe()
            .test()
            .also { router.navigateToPostSplashConfigLoader() }
            .assertNoErrors()
            .assertValueAt(0) { actual ->
                actual is NavigationEffect.Navigate.RouteBuilder
                        && actual.route == Constants.ROUTE_CONFIG_LOADER
            }
    }

    @Test
    fun `given user navigates to home screen, expected router emits RoutePopUp event with ROUTE_HOME route`() {
        router
            .observe()
            .test()
            .also { router.navigateToHomeScreen() }
            .assertNoErrors()
            .assertValueAt(0, NavigationEffect.Navigate.RoutePopUp(Constants.ROUTE_HOME))
    }

    @Test
    fun `given user navigates to server setup from splash, expected router emits RouteBuilder event with ROUTE_SERVER_SETUP route and SPLASH source`() {
        router
            .observe()
            .test()
            .also { router.navigateToServerSetup(ServerSetupLaunchSource.SPLASH) }
            .assertNoErrors()
            .assertValueAt(0) { actual ->
                val expectedRoute =
                    "${Constants.ROUTE_SERVER_SETUP}/${ServerSetupLaunchSource.SPLASH.key}"
                actual is NavigationEffect.Navigate.RouteBuilder
                        && actual.route == expectedRoute
            }
    }

    @Test
    fun `given user navigates to server setup from settings, expected router emits RouteBuilder event with ROUTE_SERVER_SETUP route and SETTINGS source`() {
        router
            .observe()
            .test()
            .also { router.navigateToServerSetup(ServerSetupLaunchSource.SETTINGS) }
            .assertNoErrors()
            .assertValueAt(0) { actual ->
                val expectedRoute =
                    "${Constants.ROUTE_SERVER_SETUP}/${ServerSetupLaunchSource.SETTINGS.key}"
                actual is NavigationEffect.Navigate.RouteBuilder
                        && actual.route == expectedRoute
            }
    }

    @Test
    fun `given user navigates to gallery details for item 5598, expected router emits Route event with ROUTE_GALLERY_DETAIL route and id 5598`() {
        router
            .observe()
            .test()
            .also { router.navigateToGalleryDetails(5598L) }
            .assertNoErrors()
            .assertValueAt(
                0,
                NavigationEffect.Navigate.Route("${Constants.ROUTE_GALLERY_DETAIL}/5598"),
            )
    }

    @Test
    fun `given user navigates to in paint, expected router emits Route event with ROUTE_IN_PAINT route`() {
        router
            .observe()
            .test()
            .also { router.navigateToInPaint() }
            .assertNoErrors()
            .assertValueAt(
                0,
                NavigationEffect.Navigate.Route(Constants.ROUTE_IN_PAINT),
            )
    }

    @Test
    fun `given user tapped hidden menu 6 times, build is debuggable, expected router emits no events`() {
        every {
            stubBuildInfoProvider.isDebug
        } returns true

        val stubObserver = router.observe().test()

        repeat(6) { router.navigateToDebugMenu() }

        stubObserver
            .assertNoErrors()
            .assertNoValues()
    }

    @Test
    fun `given user tapped hidden menu 7 times, build is debuggable, expected router emits Route event with ROUTE_DEBUG route`() {
        every {
            stubBuildInfoProvider.isDebug
        } returns true

        val stubObserver = router.observe().test()

        repeat(7) { router.navigateToDebugMenu() }

        stubObserver
            .assertNoErrors()
            .assertValueAt(0, NavigationEffect.Navigate.Route(Constants.ROUTE_DEBUG))
    }

    @Test
    fun `given user tapped hidden menu 6 times, build is NOT debuggable, expected router emits no events`() {
        every {
            stubBuildInfoProvider.isDebug
        } returns false

        val stubObserver = router.observe().test()

        repeat(6) { router.navigateToDebugMenu() }

        stubObserver
            .assertNoErrors()
            .assertNoValues()
    }

    @Test
    fun `given user tapped hidden menu 7 times, build is NOT debuggable, expected router emits no events`() {
        every {
            stubBuildInfoProvider.isDebug
        } returns false

        val stubObserver = router.observe().test()

        repeat(7) { router.navigateToDebugMenu() }

        stubObserver
            .assertNoErrors()
            .assertNoValues()
    }
}
