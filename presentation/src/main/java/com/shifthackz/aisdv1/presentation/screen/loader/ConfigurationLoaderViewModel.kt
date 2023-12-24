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
import io.reactivex.rxjava3.kotlin.subscribeBy

class ConfigurationLoaderViewModel(
    dataPreLoaderUseCase: DataPreLoaderUseCase,
    schedulersProvider: SchedulersProvider,
    analytics: Analytics,
) : MviRxViewModel<ConfigurationLoaderState, ConfigurationLoaderEffect>() {

    override val emptyState = ConfigurationLoaderState.StatusNotification(
        R.string.splash_status_initializing.asUiText()
    )

    init {
        dataPreLoaderUseCase()
            .doOnSubscribe {
                setState(
                    ConfigurationLoaderState.StatusNotification(
                        R.string.splash_status_fetching.asUiText()
                    )
                )
            }
            .subscribeOnMainThread(schedulersProvider)
            .subscribeBy(
                onError = { t ->
                    analytics.logEvent(ConfigurationLoadFailure(t.message ?: ""))
                    setState(ConfigurationLoaderState.StatusNotification("Failed loading data".asUiText()))
                    emitEffect(ConfigurationLoaderEffect.ProceedNavigation)
                    errorLog(t)
                },
                onComplete = {
                    analytics.logEvent(ConfigurationLoadSuccess)
                    setState(
                        ConfigurationLoaderState.StatusNotification(
                            R.string.splash_status_launching.asUiText()
                        )
                    )
                    emitEffect(ConfigurationLoaderEffect.ProceedNavigation)
                }
            )
            .addToDisposable()
    }
}
