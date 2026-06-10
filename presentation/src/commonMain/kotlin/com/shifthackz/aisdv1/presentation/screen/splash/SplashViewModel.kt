package com.shifthackz.aisdv1.presentation.screen.splash

import com.shifthackz.aisdv1.core.common.schedulers.DispatchersProvider
import com.shifthackz.aisdv1.core.mvi.BaseMviViewModel
import com.shifthackz.aisdv1.core.mvi.EmptyEffect
import com.shifthackz.aisdv1.core.mvi.EmptyIntent
import com.shifthackz.aisdv1.core.mvi.EmptyState
import com.shifthackz.aisdv1.domain.usecase.splash.SplashNavigationUseCase
import com.shifthackz.aisdv1.presentation.navigation.router.SplashRouter
import com.shifthackz.aisdv1.presentation.navigation.router.postSplashNavigation
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.withContext

/**
 * Coordinates `SplashViewModel` behavior in the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
class SplashViewModel(
    /**
     * Exposes the `dispatchersProvider` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val dispatchersProvider: DispatchersProvider,
    /**
     * Exposes the `splashNavigationUseCase` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val splashNavigationUseCase: SplashNavigationUseCase,
    /**
     * Exposes the `splashRouter` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val splashRouter: SplashRouter,
    /**
     * Exposes the `onError` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val onError: (Throwable) -> Unit = {},
) : BaseMviViewModel<EmptyState, EmptyIntent, EmptyEffect>(
    initialState = EmptyState,
    effectDispatcher = dispatchersProvider.immediate,
) {

    init {
        launch(dispatchersProvider.io) {
            runCatching { splashNavigationUseCase() }
                .onSuccess { action ->
                    withContext(dispatchersProvider.immediate) {
                        splashRouter.postSplashNavigation(action)
                    }
                }
                .onFailure { t ->
                    if (t is CancellationException) throw t
                    withContext(dispatchersProvider.immediate) {
                        splashRouter.navigateToServerSetupFromSplash()
                    }
                    onError(t)
                }
        }
    }

    override fun processIntent(intent: EmptyIntent) = Unit
}
