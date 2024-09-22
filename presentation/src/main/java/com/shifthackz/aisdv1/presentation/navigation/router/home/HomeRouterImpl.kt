package com.shifthackz.aisdv1.presentation.navigation.router.home

import com.shifthackz.aisdv1.presentation.navigation.NavigationEffect
import com.shifthackz.aisdv1.presentation.navigation.NavigationRoute
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject

class HomeRouterImpl : HomeRouter {

    private val effectSubject: PublishSubject<NavigationEffect.Home> = PublishSubject.create()

    override fun updateExternallyWithoutNavigation(navRoute: NavigationRoute) {
        effectSubject.onNext(NavigationEffect.Home.Update(navRoute))
    }

    override fun navigateToRoute(navRoute: NavigationRoute) {
        effectSubject.onNext(NavigationEffect.Home.Route(navRoute))
    }

    override fun navigateToTxt2Img() {
        navigateToRoute(NavigationRoute.HomeNavigation.TxtToImg)
    }

    override fun navigateToImg2Img() {
        navigateToRoute(NavigationRoute.HomeNavigation.ImgToImg)
    }

    override fun navigateToGallery() {
        navigateToRoute(NavigationRoute.HomeNavigation.Gallery)
    }

    override fun navigateToSettings() {
        navigateToRoute(NavigationRoute.HomeNavigation.Settings)
    }

    override fun observe(): Observable<NavigationEffect.Home> = effectSubject
}
