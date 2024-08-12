package com.shifthackz.aisdv1.presentation.navigation.router.main

import com.shifthackz.aisdv1.presentation.navigation.NavigationEffect
import com.shifthackz.aisdv1.presentation.screen.debug.DebugMenuAccessor
import com.shifthackz.aisdv1.presentation.screen.setup.ServerSetupLaunchSource
import com.shifthackz.aisdv1.presentation.utils.Constants
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject

internal class MainRouterImpl(
    private val debugMenuAccessor: DebugMenuAccessor,
) : MainRouter {

    private val effectSubject: PublishSubject<NavigationEffect> = PublishSubject.create()

    override fun observe(): Observable<NavigationEffect> {
        return effectSubject
    }

    override fun navigateBack() {
        effectSubject.onNext(NavigationEffect.Back)
    }

    override fun navigateToPostSplashConfigLoader() {
        effectSubject.onNext(NavigationEffect.Navigate.RouteBuilder(Constants.ROUTE_CONFIG_LOADER) {
            popUpTo(Constants.ROUTE_SPLASH) {
                inclusive = true
            }
        })
    }

    override fun navigateToHomeScreen() {
        effectSubject.onNext(NavigationEffect.Navigate.RoutePopUp(Constants.ROUTE_HOME))
    }

    override fun navigateToServerSetup(source: ServerSetupLaunchSource) {
        effectSubject.onNext(NavigationEffect.Navigate.RouteBuilder("${Constants.ROUTE_SERVER_SETUP}/${source.ordinal}") {
            if (source == ServerSetupLaunchSource.SPLASH) {
                popUpTo(Constants.ROUTE_SPLASH) {
                    inclusive = true
                }
            }
        })
    }

    override fun navigateToGalleryDetails(itemId: Long) {
        effectSubject.onNext(NavigationEffect.Navigate.Route("${Constants.ROUTE_GALLERY_DETAIL}/$itemId"))
    }

    override fun navigateToInPaint() {
        effectSubject.onNext(NavigationEffect.Navigate.Route(Constants.ROUTE_IN_PAINT))
    }

    override fun navigateToDonate() {
        effectSubject.onNext(NavigationEffect.Navigate.Route(Constants.ROUTE_DONATE))
    }

    override fun navigateToDebugMenu() {
        effectSubject.onNext(NavigationEffect.Navigate.Route(Constants.ROUTE_DEBUG))
    }

    override fun navigateToLogger() {
        effectSubject.onNext(NavigationEffect.Navigate.Route(Constants.ROUTE_LOGGER))
    }
}
