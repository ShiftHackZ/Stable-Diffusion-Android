package com.shifthackz.aisdv1.presentation.navigation.router.main

import com.shifthackz.aisdv1.presentation.model.LaunchSource
import com.shifthackz.aisdv1.presentation.navigation.NavigationEffect
import com.shifthackz.aisdv1.presentation.navigation.NavigationRoute
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
        effectSubject.onNext(NavigationEffect.Navigate.RouteBuilder(
            navRoute = NavigationRoute.Onboarding(source = source)
        ) {
            if (source == LaunchSource.SPLASH) {
                popUpTo(NavigationRoute.Splash) {
                    inclusive = true
                }
            }
        })
    }

    override fun navigateToPostSplashConfigLoader() {
        effectSubject.onNext(NavigationEffect.Navigate.RouteBuilder(NavigationRoute.ConfigLoader) {
            popUpTo(NavigationRoute.Splash) {
                inclusive = true
            }
        })
    }

    override fun navigateToHomeScreen() {
        effectSubject.onNext(NavigationEffect.Navigate.RouteBuilder(NavigationRoute.Home) {
            popUpTo(0) {
                inclusive = true
            }
        })
    }

    override fun navigateToServerSetup(source: LaunchSource) {
        effectSubject.onNext(
            NavigationEffect.Navigate.RouteBuilder(
                navRoute = NavigationRoute.ServerSetup(source = source)
            ) {
                if (source == LaunchSource.SPLASH) {
                    popUpTo(0) {
                        inclusive = true
                    }
                }
            })
    }

    override fun navigateToGalleryDetails(itemId: Long) {
        effectSubject.onNext(
            NavigationEffect.Navigate.Route(
                navRoute = NavigationRoute.GalleryDetail(itemId = itemId)
            )
        )
    }

    override fun navigateToReportImage(itemId: Long) {
        effectSubject.onNext(
            NavigationEffect.Navigate.Route(
                navRoute = NavigationRoute.ReportImage(itemId = itemId)
            )
        )
    }

    override fun navigateToInPaint() {
        effectSubject.onNext(NavigationEffect.Navigate.Route(navRoute = NavigationRoute.InPaint))
    }

    override fun navigateToDonate() {
        effectSubject.onNext(NavigationEffect.Navigate.Route(navRoute = NavigationRoute.Donate))
    }

    override fun navigateToDebugMenu() {
        effectSubject.onNext(NavigationEffect.Navigate.Route(navRoute = NavigationRoute.Debug))
    }

    override fun navigateToLogger() {
        effectSubject.onNext(NavigationEffect.Navigate.Route(navRoute = NavigationRoute.Logger))
    }
}
