package com.shifthackz.aisdv1.presentation.navigation.router.main

import com.shifthackz.aisdv1.presentation.model.LaunchSource
import com.shifthackz.aisdv1.presentation.navigation.NavigationEffect
import com.shifthackz.aisdv1.presentation.utils.Constants
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject

internal class MainRouterImpl : MainRouter {

    private val effectSubject: PublishSubject<NavigationEffect> = PublishSubject.create()

    override fun observe(): Observable<NavigationEffect> {
        return effectSubject
    }

    override fun navigateBack() {
        effectSubject.onNext(NavigationEffect.Back)
    }

    override fun navigateToOnBoarding(source: LaunchSource) {
        effectSubject.onNext(NavigationEffect.Navigate.RouteBuilder("${Constants.ROUTE_ONBOARDING}/${source.ordinal}") {
            if (source == LaunchSource.SPLASH) {
                popUpTo(Constants.ROUTE_SPLASH) {
                    inclusive = true
                }
            }
        })
    }

    override fun navigateToPostSplashConfigLoader() {
        effectSubject.onNext(NavigationEffect.Navigate.RouteBuilder(Constants.ROUTE_CONFIG_LOADER) {
            popUpTo(Constants.ROUTE_SPLASH) {
                inclusive = true
            }
        })
    }

    override fun navigateToHomeScreen() {
        effectSubject.onNext(NavigationEffect.Navigate.RouteBuilder(Constants.ROUTE_HOME) {
            popUpTo(0) {
                inclusive = true
            }
        })
    }

    override fun navigateToServerSetup(source: LaunchSource) {
        effectSubject.onNext(NavigationEffect.Navigate.RouteBuilder("${Constants.ROUTE_SERVER_SETUP}/${source.ordinal}") {
            if (source == LaunchSource.SPLASH) {
                popUpTo(Constants.ROUTE_ONBOARDING) {
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
