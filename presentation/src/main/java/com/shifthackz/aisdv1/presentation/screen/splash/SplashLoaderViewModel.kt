package com.shifthackz.aisdv1.presentation.screen.splash

import com.shifthackz.aisdv1.core.common.schedulers.SchedulersProvider
import com.shifthackz.aisdv1.core.common.schedulers.subscribeOnMainThread
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.core.viewmodel.MviRxViewModel
import com.shifthackz.aisdv1.domain.usecase.caching.DataPreLoaderUseCase
import com.shifthackz.aisdv1.presentation.R
import io.reactivex.rxjava3.kotlin.subscribeBy

class SplashLoaderViewModel(
    dataPreLoaderUseCase: DataPreLoaderUseCase,
    schedulersProvider: SchedulersProvider,
) : MviRxViewModel<SplashLoaderState, SplashLoaderEffect>() {

    override val emptyState = SplashLoaderState.StatusNotification(
        R.string.splash_status_initializing.asUiText()
    )

    init {
        dataPreLoaderUseCase()
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
                    setState(SplashLoaderState.StatusNotification("Failed loading data".asUiText()))
                    emitEffect(SplashLoaderEffect.ProceedNavigation)

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
