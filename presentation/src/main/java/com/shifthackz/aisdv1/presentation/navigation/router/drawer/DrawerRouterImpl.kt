package com.shifthackz.aisdv1.presentation.navigation.router.drawer

import com.shifthackz.aisdv1.presentation.navigation.NavigationEffect
import io.reactivex.rxjava3.subjects.PublishSubject

internal class DrawerRouterImpl : DrawerRouter {

    private val effectSubject: PublishSubject<NavigationEffect.Drawer> = PublishSubject.create()

    override fun observe() = effectSubject

    override fun openDrawer() {
        effectSubject.onNext(NavigationEffect.Drawer.Open)
    }

    override fun closeDrawer() {
        effectSubject.onNext(NavigationEffect.Drawer.Close)
    }
}
