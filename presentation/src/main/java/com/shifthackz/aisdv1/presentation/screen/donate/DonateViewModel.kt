package com.shifthackz.aisdv1.presentation.screen.donate

import com.shifthackz.aisdv1.core.common.log.errorLog
import com.shifthackz.aisdv1.core.common.schedulers.SchedulersProvider
import com.shifthackz.aisdv1.core.common.schedulers.subscribeOnMainThread
import com.shifthackz.aisdv1.core.viewmodel.MviRxViewModel
import com.shifthackz.aisdv1.domain.usecase.donate.FetchAndGetSupportersUseCase
import com.shifthackz.aisdv1.presentation.navigation.router.main.MainRouter
import io.reactivex.rxjava3.kotlin.subscribeBy

class DonateViewModel(
    fetchAndGetSupportersUseCase: FetchAndGetSupportersUseCase,
    schedulersProvider: SchedulersProvider,
    private val mainRouter: MainRouter,
) : MviRxViewModel<DonateState, DonateIntent, DonateEffect>() {

    override val initialState = DonateState()

    init {
        !fetchAndGetSupportersUseCase()
            .subscribeOnMainThread(schedulersProvider)
            .subscribeBy(
                onError = { t ->
                    updateState { it.copy(loading = false) }
                    errorLog(t)
                },
                onSuccess = { supporters ->
                    updateState {
                        it.copy(
                            loading = false,
                            supporters = supporters,
                        )
                    }
                },
            )
    }

    override fun processIntent(intent: DonateIntent) {
        when (intent) {
            is DonateIntent.LaunchUrl -> emitEffect(DonateEffect.OpenUrl(intent.url))
            DonateIntent.NavigateBack -> mainRouter.navigateBack()
        }
    }
}
