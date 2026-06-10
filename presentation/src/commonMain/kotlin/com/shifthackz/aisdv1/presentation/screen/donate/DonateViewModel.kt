package com.shifthackz.aisdv1.presentation.screen.donate

import com.shifthackz.aisdv1.core.common.links.LinksProvider
import com.shifthackz.aisdv1.core.common.schedulers.DispatchersProvider
import com.shifthackz.aisdv1.core.mvi.BaseMviViewModel
import com.shifthackz.aisdv1.domain.usecase.donate.FetchSupportersUseCase
import com.shifthackz.aisdv1.presentation.navigation.router.DonateRouter
import kotlinx.coroutines.withContext

/**
 * Coordinates `DonateViewModel` behavior in the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
class DonateViewModel(
    /**
     * Exposes the `dispatchersProvider` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val dispatchersProvider: DispatchersProvider,
    /**
     * Exposes the `fetchSupportersUseCase` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val fetchSupportersUseCase: FetchSupportersUseCase,
    /**
     * Exposes the `linksProvider` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val linksProvider: LinksProvider,
    /**
     * Exposes the `router` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val router: DonateRouter,
    /**
     * Exposes the `onError` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
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
