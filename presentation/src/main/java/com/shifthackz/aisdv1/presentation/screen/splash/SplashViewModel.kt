package com.shifthackz.aisdv1.presentation.screen.splash

import com.shifthackz.aisdv1.core.common.log.errorLog
import com.shifthackz.aisdv1.core.common.schedulers.SchedulersProvider
import com.shifthackz.aisdv1.core.common.schedulers.subscribeOnMainThread
import com.shifthackz.aisdv1.core.ui.EmptyEffect
import com.shifthackz.aisdv1.core.ui.EmptyIntent
import com.shifthackz.aisdv1.core.ui.EmptyState
import com.shifthackz.aisdv1.core.viewmodel.MviRxViewModel
import com.shifthackz.aisdv1.domain.usecase.splash.SplashNavigationUseCase
import com.shifthackz.aisdv1.presentation.navigation.Router
import com.shifthackz.aisdv1.presentation.screen.setup.ServerSetupLaunchSource
import io.reactivex.rxjava3.kotlin.subscribeBy

class SplashViewModel(
    router: Router,
    splashNavigationUseCase: SplashNavigationUseCase,
    schedulersProvider: SchedulersProvider,
) : MviRxViewModel<EmptyState, EmptyIntent, EmptyEffect>() {

    override val emptyState = EmptyState

    init {
        !splashNavigationUseCase()
            .subscribeOnMainThread(schedulersProvider)
            .subscribeBy(::errorLog) { action ->
                when (action) {
                    SplashNavigationUseCase.Action.LAUNCH_ONBOARDING -> {}
                    SplashNavigationUseCase.Action.LAUNCH_SERVER_SETUP -> router.navigateToServerSetup(
                        source = ServerSetupLaunchSource.SPLASH
                    )
                    SplashNavigationUseCase.Action.LAUNCH_HOME -> router.navigateToPostSplashConfigLoader()
                }
            }
    }
}
