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

class SplashViewModel(
    private val dispatchersProvider: DispatchersProvider,
    private val splashNavigationUseCase: SplashNavigationUseCase,
    private val splashRouter: SplashRouter,
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
