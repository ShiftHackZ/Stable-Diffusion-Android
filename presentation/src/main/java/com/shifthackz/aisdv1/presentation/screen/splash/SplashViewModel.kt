package com.shifthackz.aisdv1.presentation.screen.splash

import com.shifthackz.aisdv1.core.common.log.errorLog
import com.shifthackz.aisdv1.core.common.schedulers.SchedulersProvider
import com.shifthackz.aisdv1.core.common.schedulers.subscribeOnMainThread
import com.shifthackz.aisdv1.core.ui.EmptyState
import com.shifthackz.aisdv1.core.viewmodel.MviRxViewModel
import com.shifthackz.aisdv1.domain.usecase.splash.SplashNavigationUseCase
import io.reactivex.rxjava3.kotlin.subscribeBy

class SplashViewModel(
    splashNavigationUseCase: SplashNavigationUseCase,
    schedulersProvider: SchedulersProvider,
) : MviRxViewModel<EmptyState, SplashEffect>() {

    override val emptyState = EmptyState

    init {
        !splashNavigationUseCase()
            .map(SplashNavigationUseCase.Action::toEffect)
            .subscribeOnMainThread(schedulersProvider)
            .subscribeBy(::errorLog, ::emitEffect)
    }
}
