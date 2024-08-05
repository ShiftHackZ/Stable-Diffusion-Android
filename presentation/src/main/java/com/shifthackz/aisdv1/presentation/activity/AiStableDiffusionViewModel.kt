package com.shifthackz.aisdv1.presentation.activity

import com.shifthackz.aisdv1.core.common.extensions.EmptyLambda
import com.shifthackz.aisdv1.core.common.log.errorLog
import com.shifthackz.aisdv1.core.common.schedulers.SchedulersProvider
import com.shifthackz.aisdv1.core.common.schedulers.subscribeOnMainThread
import com.shifthackz.aisdv1.core.viewmodel.MviRxViewModel
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.presentation.navigation.NavigationEffect
import com.shifthackz.aisdv1.presentation.navigation.graph.mainDrawerNavItems
import com.shifthackz.aisdv1.presentation.navigation.router.drawer.DrawerRouter
import com.shifthackz.aisdv1.presentation.navigation.router.home.HomeRouter
import com.shifthackz.aisdv1.presentation.navigation.router.main.MainRouter
import io.reactivex.rxjava3.kotlin.subscribeBy

class AiStableDiffusionViewModel(
    schedulersProvider: SchedulersProvider,
    mainRouter: MainRouter,
    drawerRouter: DrawerRouter,
    private val homeRouter: HomeRouter,
    private val preferenceManager: PreferenceManager,
) : MviRxViewModel<AppState, AppIntent, NavigationEffect>() {

    override val initialState = AppState()

    init {
        !mainRouter.observe()
            .subscribeOnMainThread(schedulersProvider)
            .subscribeBy(::errorLog, EmptyLambda, ::emitEffect)

        !drawerRouter.observe()
            .subscribeOnMainThread(schedulersProvider)
            .subscribeBy(::errorLog, EmptyLambda, ::emitEffect)

        !homeRouter.observe()
            .subscribeOnMainThread(schedulersProvider)
            .subscribeBy(::errorLog, EmptyLambda, ::emitEffect)

        !preferenceManager.observe()
            .map(::mainDrawerNavItems)
            .subscribeOnMainThread(schedulersProvider)
            .subscribeBy(::errorLog, EmptyLambda) { drawerItems ->
                updateState { state ->
                    state.copy(drawerItems = drawerItems)
                }
            }
    }

    override fun processIntent(intent: AppIntent) = when (intent) {
        AppIntent.GrantStoragePermission -> {
            preferenceManager.saveToMediaStore = true
        }

        is AppIntent.HomeRoute -> {
            homeRouter.navigateToRoute(intent.route)
        }
    }
}
