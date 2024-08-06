package com.shifthackz.aisdv1.presentation.navigation.router.home

import com.shifthackz.aisdv1.presentation.navigation.NavigationEffect
import com.shifthackz.aisdv1.presentation.utils.Constants
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject

class HomeRouterImpl : HomeRouter {

    private val effectSubject: PublishSubject<NavigationEffect.Home> = PublishSubject.create()

    override fun updateExternallyWithoutNavigation(route: String) {
        effectSubject.onNext(NavigationEffect.Home.Update(route))
    }

    override fun navigateToRoute(route: String) {
        effectSubject.onNext(NavigationEffect.Home.Route(route))
    }

    override fun navigateToTxt2Img() {
        navigateToRoute(Constants.ROUTE_TXT_TO_IMG)
    }

    override fun navigateToImg2Img() {
        navigateToRoute(Constants.ROUTE_IMG_TO_IMG)
    }

    override fun navigateToGallery() {
        navigateToRoute(Constants.ROUTE_GALLERY)
    }

    override fun navigateToSettings() {
        navigateToRoute(Constants.ROUTE_SETTINGS)
    }

    override fun observe(): Observable<NavigationEffect.Home> = effectSubject
}
