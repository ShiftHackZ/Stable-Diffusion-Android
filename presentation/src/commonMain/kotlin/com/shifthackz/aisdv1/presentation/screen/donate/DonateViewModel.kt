package com.shifthackz.aisdv1.presentation.screen.donate

import com.shifthackz.aisdv1.core.common.links.LinksProvider
import com.shifthackz.aisdv1.core.common.schedulers.DispatchersProvider
import com.shifthackz.aisdv1.core.mvi.BaseMviViewModel
import com.shifthackz.aisdv1.domain.usecase.donate.FetchSupportersUseCase
import com.shifthackz.aisdv1.presentation.navigation.router.DonateRouter
import kotlinx.coroutines.withContext

class DonateViewModel(
    private val dispatchersProvider: DispatchersProvider,
    private val fetchSupportersUseCase: FetchSupportersUseCase,
    private val linksProvider: LinksProvider,
    private val router: DonateRouter,
    private val onError: (Throwable) -> Unit = {},
) : BaseMviViewModel<DonateState, DonateIntent, DonateEffect>(
    initialState = DonateState(),
    effectDispatcher = dispatchersProvider.immediate,
) {

    init {
        launch(dispatchersProvider.io) {
            runCatching { fetchSupportersUseCase() }
                .onSuccess { supporters ->
                    withContext(dispatchersProvider.immediate) {
                        updateState {
                            it.copy(
                                loading = false,
                                supporters = supporters,
                            )
                        }
                    }
                }
                .onFailure { t ->
                    withContext(dispatchersProvider.immediate) {
                        updateState { it.copy(loading = false) }
                    }
                    onError(t)
                }
        }
    }

    override fun processIntent(intent: DonateIntent) {
        when (intent) {
            DonateIntent.LaunchDonate -> emitEffect(DonateEffect.OpenUrl(linksProvider.donateUrl))
            DonateIntent.NavigateBack -> router.navigateBack()
        }
    }
}
