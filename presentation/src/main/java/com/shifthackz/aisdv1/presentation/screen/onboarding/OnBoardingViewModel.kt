package com.shifthackz.aisdv1.presentation.screen.onboarding

import com.shifthackz.aisdv1.core.common.log.errorLog
import com.shifthackz.aisdv1.core.common.schedulers.SchedulersProvider
import com.shifthackz.aisdv1.core.common.schedulers.subscribeOnMainThread
import com.shifthackz.aisdv1.core.viewmodel.MviRxViewModel
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.usecase.splash.SplashNavigationUseCase
import com.shifthackz.aisdv1.presentation.navigation.router.main.MainRouter
import com.shifthackz.aisdv1.presentation.navigation.router.main.postSplashNavigation
import com.shifthackz.android.core.mvi.EmptyEffect
import com.shifthackz.android.core.mvi.EmptyState
import io.reactivex.rxjava3.kotlin.subscribeBy

class OnBoardingViewModel(
    private val mainRouter: MainRouter,
    private val splashNavigationUseCase: SplashNavigationUseCase,
    private val preferenceManager: PreferenceManager,
    private val schedulersProvider: SchedulersProvider,
) : MviRxViewModel<EmptyState, OnBoardingIntent, EmptyEffect>() {

    override val initialState = EmptyState

    override fun processIntent(intent: OnBoardingIntent) {
        when (intent) {
            OnBoardingIntent.Navigate -> {
                preferenceManager.onBoardingComplete = true
                !splashNavigationUseCase()
                    .subscribeOnMainThread(schedulersProvider)
                    .subscribeBy(::errorLog) { action -> mainRouter.postSplashNavigation(action) }
            }
        }
    }
}
