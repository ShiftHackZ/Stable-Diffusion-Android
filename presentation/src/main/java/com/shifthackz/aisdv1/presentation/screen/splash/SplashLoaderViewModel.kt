package com.shifthackz.aisdv1.presentation.screen.splash

import com.shifthackz.aisdv1.core.common.schedulers.SchedulersProvider
import com.shifthackz.aisdv1.core.common.schedulers.subscribeOnMainThread
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.core.viewmodel.MviRxViewModel
import com.shifthackz.aisdv1.domain.usecase.DataPreLoaderUseCase
import com.shifthackz.aisdv1.presentation.R
import com.shifthackz.aisdv1.presentation.screen.splash.contract.SplashLoaderEffect
import com.shifthackz.aisdv1.presentation.screen.splash.contract.SplashLoaderState
import io.reactivex.rxjava3.kotlin.subscribeBy

class SplashLoaderViewModel(
    dataPreLoaderUseCase: DataPreLoaderUseCase,
    schedulersProvider: SchedulersProvider,
) : MviRxViewModel<SplashLoaderState, SplashLoaderEffect>() {

    override val emptyState = SplashLoaderState.StatusNotification(
        R.string.splash_status_initializing.asUiText()
    )

    init {
        dataPreLoaderUseCase
            .execute()
            .doOnSubscribe {
                setState(
                    SplashLoaderState.StatusNotification(
                        R.string.splash_status_fetching.asUiText()
                    )
                )
            }
            .subscribeOnMainThread(schedulersProvider)
            .subscribeBy(
                onError = {
                    it.printStackTrace()
                    //setState(SplashLoaderState.StatusNotification("Failed loading data".asUiText()))
                },
                onComplete = {
                    setState(
                        SplashLoaderState.StatusNotification(
                            R.string.splash_status_launching.asUiText()
                        )
                    )
                    emitEffect(SplashLoaderEffect.ProceedNavigation)
                }
            )
            .addToDisposable()
    }

}
