package com.shifthackz.aisdv1.presentation.screen.loader

import com.shifthackz.aisdv1.core.common.log.errorLog
import com.shifthackz.aisdv1.core.common.schedulers.SchedulersProvider
import com.shifthackz.aisdv1.core.common.schedulers.subscribeOnMainThread
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.core.viewmodel.MviRxViewModel
import com.shifthackz.aisdv1.domain.usecase.caching.DataPreLoaderUseCase
import com.shifthackz.aisdv1.presentation.R
import com.shifthackz.aisdv1.presentation.navigation.router.main.MainRouter
import com.shifthackz.android.core.mvi.EmptyEffect
import com.shifthackz.android.core.mvi.EmptyIntent
import io.reactivex.rxjava3.kotlin.subscribeBy

class ConfigurationLoaderViewModel(
    dataPreLoaderUseCase: DataPreLoaderUseCase,
    schedulersProvider: SchedulersProvider,
    mainRouter: MainRouter,
) : MviRxViewModel<ConfigurationLoaderState, EmptyIntent, EmptyEffect>() {

    override val initialState = ConfigurationLoaderState.StatusNotification(
        R.string.splash_status_initializing.asUiText()
    )

    init {
        !dataPreLoaderUseCase()
            .doOnSubscribe {
                updateState {
                    ConfigurationLoaderState.StatusNotification(
                        R.string.splash_status_fetching.asUiText()
                    )
                }
            }
            .subscribeOnMainThread(schedulersProvider)
            .subscribeBy(
                onError = { t ->
                    updateState {
                        ConfigurationLoaderState.StatusNotification("Failed loading data".asUiText())
                    }
                    mainRouter.navigateToHomeScreen()
                    errorLog(t)
                },
                onComplete = {
                    updateState {
                        ConfigurationLoaderState.StatusNotification(
                            R.string.splash_status_launching.asUiText()
                        )
                    }
                    mainRouter.navigateToHomeScreen()
                },
            )
    }
}
