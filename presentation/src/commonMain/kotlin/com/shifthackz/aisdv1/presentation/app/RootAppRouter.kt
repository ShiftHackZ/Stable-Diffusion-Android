package com.shifthackz.aisdv1.presentation.app

import com.shifthackz.aisdv1.presentation.model.LaunchSource
import com.shifthackz.aisdv1.presentation.navigation.router.ConfigurationLoaderRouter
import com.shifthackz.aisdv1.presentation.navigation.router.DebugMenuRouter
import com.shifthackz.aisdv1.presentation.navigation.router.DonateRouter
import com.shifthackz.aisdv1.presentation.navigation.router.GalleryDetailRouter
import com.shifthackz.aisdv1.presentation.navigation.router.GalleryRouter
import com.shifthackz.aisdv1.presentation.navigation.router.HistoryRouter
import com.shifthackz.aisdv1.presentation.navigation.router.HomeRouter
import com.shifthackz.aisdv1.presentation.navigation.router.ImageToImageRouter
import com.shifthackz.aisdv1.presentation.navigation.router.LoggerRouter
import com.shifthackz.aisdv1.presentation.navigation.router.OnBoardingRouter
import com.shifthackz.aisdv1.presentation.navigation.router.ReportRouter
import com.shifthackz.aisdv1.presentation.navigation.router.ServerSetupRouter
import com.shifthackz.aisdv1.presentation.navigation.router.SettingsRouter
import com.shifthackz.aisdv1.presentation.navigation.router.SplashRouter
import com.shifthackz.aisdv1.presentation.navigation.router.TextToImageRouter
import com.shifthackz.aisdv1.presentation.navigation.router.WebUiRouter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Defines the `AppRoute` contract for the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
sealed interface AppRoute {
    /**
     * Provides the `Splash` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object Splash : AppRoute
    /**
     * Carries `OnBoarding` data through the SDAI presentation layer.
     *
     * @param source source value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class OnBoarding(val source: LaunchSource = LaunchSource.SPLASH) : AppRoute
    /**
     * Carries `Setup` data through the SDAI presentation layer.
     *
     * @param source source value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class Setup(val source: LaunchSource = LaunchSource.SPLASH) : AppRoute
    /**
     * Provides the `ConfigurationLoader` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object ConfigurationLoader : AppRoute
    /**
     * Provides the `Home` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object Home : AppRoute
    /**
     * Provides the `TextToImage` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object TextToImage : AppRoute
    /**
     * Provides the `ImageToImage` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object ImageToImage : AppRoute
    /**
     * Provides the `ImageInPaint` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object ImageInPaint : AppRoute
    /**
     * Provides the `Gallery` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object Gallery : AppRoute
    /**
     * Carries `GalleryDetail` data through the SDAI presentation layer.
     *
     * @param itemId item id value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class GalleryDetail(val itemId: Long) : AppRoute
    /**
     * Provides the `Settings` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object Settings : AppRoute
    /**
     * Provides the `History` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object History : AppRoute
    /**
     * Carries `Report` data through the SDAI presentation layer.
     *
     * @param itemId item id value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class Report(val itemId: Long) : AppRoute
    /**
     * Provides the `Debug` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object Debug : AppRoute
    /**
     * Provides the `Logger` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object Logger : AppRoute
    /**
     * Provides the `Donate` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object Donate : AppRoute
    /**
     * Provides the `WebUi` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object WebUi : AppRoute
}

/**
 * Coordinates `RootAppRouter` behavior in the SDAI presentation layer.
 *
 * @param initialRoute initial route value consumed by the API.
 * @author Dmitriy Moroz
 */
class RootAppRouter(
    initialRoute: AppRoute = AppRoute.Splash,
) : ServerSetupRouter,
    ConfigurationLoaderRouter,
    SplashRouter,
    DonateRouter,
    HomeRouter,
    TextToImageRouter,
    ImageToImageRouter,
    HistoryRouter,
    GalleryRouter,
    GalleryDetailRouter,
    DebugMenuRouter,
    LoggerRouter,
    ReportRouter,
    SettingsRouter,
    WebUiRouter,
    OnBoardingRouter {

    private val routeState = MutableStateFlow(initialRoute)
    val route = routeState.asStateFlow()

    private val drawerOpenState = MutableStateFlow(false)
    val drawerOpen = drawerOpenState.asStateFlow()

    private val routeStack = mutableListOf(initialRoute)
    private val canNavigateBackState = MutableStateFlow(canNavigateBackFrom(initialRoute))
    val canNavigateBack = canNavigateBackState.asStateFlow()

    override fun navigateBack() {
        if (drawerOpenState.value) {
            closeDrawer()
            return
        }

        closeDrawer()
        if (routeStack.size > 1) {
            routeStack.removeAt(routeStack.lastIndex)
            setRoute(routeStack.last())
        } else if (routeStack.lastOrNull()?.canFallBackToTextToImage() == true) {
            replaceRoute(AppRoute.TextToImage)
        }
    }

    override fun navigateToPostSetupConfigLoader() {
        replaceRoute(AppRoute.ConfigurationLoader)
    }

    override fun navigateToHomeScreen() {
        replaceRoute(AppRoute.TextToImage)
    }

    override fun navigateToOnBoardingFromSplash() {
        replaceRoute(AppRoute.OnBoarding(LaunchSource.SPLASH))
    }

    override fun navigateToServerSetupFromSplash() {
        replaceRoute(AppRoute.Setup(LaunchSource.SPLASH))
    }

    override fun navigateToPostSplashConfigLoader() {
        replaceRoute(AppRoute.ConfigurationLoader)
    }

    override fun navigateToServerSetup() {
        navigateTo(AppRoute.Setup(LaunchSource.SETTINGS))
    }

    override fun navigateToServerSetup(source: LaunchSource) {
        navigateTo(AppRoute.Setup(source))
    }

    override fun navigateToServerSetupAfterOnBoarding() {
        replaceRoute(AppRoute.Setup(LaunchSource.SPLASH))
    }

    override fun navigateToTextToImage() {
        navigateToGenerationHomeRoute(AppRoute.TextToImage)
    }

    override fun navigateToImageToImage() {
        navigateToGenerationHomeRoute(AppRoute.ImageToImage)
    }

    override fun navigateToImageInPaint() {
        navigateTo(AppRoute.ImageInPaint)
    }

    override fun navigateToGallery() {
        navigateToHomeRoute(AppRoute.Gallery)
    }

    override fun navigateToSettings() {
        navigateToHomeRoute(AppRoute.Settings)
    }

    override fun navigateToHistory() {
        navigateTo(AppRoute.History)
    }

    override fun openDrawer() {
        drawerOpenState.value = true
    }

    override fun closeDrawer() {
        drawerOpenState.value = false
    }

    override fun navigateToDebugMenu() {
        navigateTo(AppRoute.Debug)
    }

    override fun navigateToDonate() {
        navigateTo(AppRoute.Donate)
    }

    override fun navigateToOnBoarding(source: LaunchSource) {
        navigateTo(AppRoute.OnBoarding(source))
    }

    override fun navigateToPostOnBoardingConfigLoader() {
        replaceRoute(AppRoute.ConfigurationLoader)
    }

    override fun navigateToGalleryDetails(itemId: Long) {
        navigateTo(AppRoute.GalleryDetail(itemId))
    }

    override fun navigateToReportImage(itemId: Long) {
        navigateTo(AppRoute.Report(itemId))
    }

    override fun navigateToLogger() {
        navigateTo(AppRoute.Logger)
    }

    fun navigateToWebUi() {
        navigateTo(AppRoute.WebUi)
    }

    private fun navigateTo(route: AppRoute) {
        closeDrawer()
        if (routeState.value == route) return
        routeStack.add(route)
        setRoute(route)
    }

    private fun navigateToHomeRoute(route: AppRoute) {
        closeDrawer()
        if (routeState.value == route) return
        if (routeStack.lastOrNull()?.isHomeRoute() == true) {
            replaceRoute(route)
        } else {
            navigateTo(route)
        }
    }

    private fun navigateToGenerationHomeRoute(route: AppRoute) {
        if (routeStack.lastOrNull() is AppRoute.GalleryDetail) {
            replaceRoute(route)
        } else {
            navigateToHomeRoute(route)
        }
    }

    private fun replaceRoute(route: AppRoute) {
        closeDrawer()
        if (routeStack.isEmpty()) {
            routeStack.add(route)
        } else {
            routeStack[routeStack.lastIndex] = route
        }
        setRoute(route)
    }

    private fun setRoute(route: AppRoute) {
        routeState.value = route
        canNavigateBackState.value = canNavigateBackFrom(route)
    }

    private fun canNavigateBackFrom(route: AppRoute): Boolean {
        return routeStack.size > 1 || route.canFallBackToTextToImage()
    }
}

/**
 * Executes the `isHomeRoute` step in the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
fun AppRoute.isHomeRoute(): Boolean = when (this) {
    AppRoute.Home,
    AppRoute.TextToImage,
    AppRoute.ImageToImage,
    AppRoute.Gallery,
    AppRoute.Settings,
    -> true

    AppRoute.Splash,
    is AppRoute.OnBoarding,
    is AppRoute.Setup,
    AppRoute.ConfigurationLoader,
    is AppRoute.GalleryDetail,
    AppRoute.ImageInPaint,
    AppRoute.History,
    is AppRoute.Report,
    AppRoute.Debug,
    AppRoute.Logger,
    AppRoute.Donate,
    AppRoute.WebUi,
    -> false
}

/**
 * Executes the `canFallBackToTextToImage` step in the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
private fun AppRoute.canFallBackToTextToImage(): Boolean = when (this) {
    AppRoute.ImageToImage,
    AppRoute.ImageInPaint,
    AppRoute.Gallery,
    AppRoute.Settings,
    AppRoute.History,
    is AppRoute.GalleryDetail,
    is AppRoute.Report,
    AppRoute.Debug,
    AppRoute.Logger,
    AppRoute.Donate,
    AppRoute.WebUi,
    -> true

    AppRoute.Home,
    AppRoute.TextToImage,
    AppRoute.Splash,
    is AppRoute.OnBoarding,
    is AppRoute.Setup,
    AppRoute.ConfigurationLoader,
    -> false
}
