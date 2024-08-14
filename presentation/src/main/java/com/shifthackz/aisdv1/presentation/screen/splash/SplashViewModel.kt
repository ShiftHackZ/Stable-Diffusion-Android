package com.shifthackz.aisdv1.presentation.screen.splash

import com.shifthackz.aisdv1.core.common.log.errorLog
import com.shifthackz.aisdv1.core.common.schedulers.SchedulersProvider
import com.shifthackz.aisdv1.core.common.schedulers.subscribeOnMainThread
import com.shifthackz.aisdv1.core.viewmodel.MviRxViewModel
import com.shifthackz.aisdv1.domain.usecase.splash.SplashNavigationUseCase
import com.shifthackz.aisdv1.presentation.navigation.router.main.MainRouter
import com.shifthackz.aisdv1.presentation.navigation.router.main.postSplashNavigation
import com.shifthackz.android.core.mvi.EmptyEffect
import com.shifthackz.android.core.mvi.EmptyIntent
import com.shifthackz.android.core.mvi.EmptyState
import io.reactivex.rxjava3.kotlin.subscribeBy

class SplashViewModel(
    mainRouter: MainRouter,
    splashNavigationUseCase: SplashNavigationUseCase,
    schedulersProvider: SchedulersProvider,
) : MviRxViewModel<EmptyState, EmptyIntent, EmptyEffect>() {

    override val initialState = EmptyState

    init {
        !splashNavigationUseCase()
            .subscribeOnMainThread(schedulersProvider)
            .subscribeBy(::errorLog) { action -> mainRouter.postSplashNavigation(action) }
    }
}
