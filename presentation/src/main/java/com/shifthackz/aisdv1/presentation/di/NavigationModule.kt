package com.shifthackz.aisdv1.presentation.di

import com.shifthackz.aisdv1.presentation.navigation.router.drawer.DrawerRouter
import com.shifthackz.aisdv1.presentation.navigation.router.drawer.DrawerRouterImpl
import com.shifthackz.aisdv1.presentation.navigation.router.home.HomeRouter
import com.shifthackz.aisdv1.presentation.navigation.router.home.HomeRouterImpl
import com.shifthackz.aisdv1.presentation.navigation.router.main.MainRouter
import com.shifthackz.aisdv1.presentation.navigation.router.main.MainRouterImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val navigationModule = module {
    singleOf(::MainRouterImpl) bind MainRouter::class
    singleOf(::DrawerRouterImpl) bind DrawerRouter::class
    singleOf(::HomeRouterImpl) bind HomeRouter::class
}
