package com.shifthackz.aisdv1.presentation.screen.loader

import com.shifthackz.aisdv1.core.common.log.errorLog
import com.shifthackz.aisdv1.core.common.schedulers.SchedulersProvider
import com.shifthackz.aisdv1.core.common.schedulers.subscribeOnMainThread
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.core.viewmodel.MviRxViewModel
import com.shifthackz.aisdv1.domain.feature.analytics.Analytics
import com.shifthackz.aisdv1.domain.usecase.caching.DataPreLoaderUseCase
import com.shifthackz.aisdv1.presentation.R
import com.shifthackz.aisdv1.presentation.features.ConfigurationLoadFailure
import com.shifthackz.aisdv1.presentation.features.ConfigurationLoadSuccess
import com.shifthackz.aisdv1.presentation.navigation.Router
import com.shifthackz.android.core.mvi.EmptyEffect
import com.shifthackz.android.core.mvi.EmptyIntent
import io.reactivex.rxjava3.kotlin.subscribeBy

class ConfigurationLoaderViewModel(
    dataPreLoaderUseCase: DataPreLoaderUseCase,
    schedulersProvider: SchedulersProvider,
    analytics: Analytics,
    router: Router,
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
                    analytics.logEvent(ConfigurationLoadFailure(t.message ?: ""))
                    updateState {
                        ConfigurationLoaderState.StatusNotification("Failed loading data".asUiText())
                    }
                    router.navigateToHomeScreen()
                    errorLog(t)
                },
                onComplete = {
                    analytics.logEvent(ConfigurationLoadSuccess)
                    updateState {
                        ConfigurationLoaderState.StatusNotification(
                            R.string.splash_status_launching.asUiText()
                        )
                    }
                    router.navigateToHomeScreen()
                },
            )
    }
}
