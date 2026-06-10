package com.shifthackz.aisdv1.presentation.screen.onboarding

import com.shifthackz.aisdv1.core.common.appbuild.BuildInfoProvider
import com.shifthackz.aisdv1.core.common.schedulers.DispatchersProvider
import com.shifthackz.aisdv1.core.mvi.BaseMviViewModel
import com.shifthackz.aisdv1.core.mvi.EmptyEffect
import com.shifthackz.aisdv1.domain.entity.DarkThemeToken
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.usecase.splash.SplashNavigationUseCase
import com.shifthackz.aisdv1.presentation.model.LaunchSource
import com.shifthackz.aisdv1.presentation.navigation.router.OnBoardingRouter
import com.shifthackz.aisdv1.presentation.navigation.router.postOnBoardingNavigation
import kotlinx.coroutines.withContext

class OnBoardingViewModel(
    val launchSource: LaunchSource,
    private val dispatchersProvider: DispatchersProvider,
    private val router: OnBoardingRouter,
    private val splashNavigationUseCase: SplashNavigationUseCase,
    private val preferenceManager: PreferenceManager,
    private val buildInfoProvider: BuildInfoProvider,
) : BaseMviViewModel<OnBoardingState, OnBoardingIntent, EmptyEffect>(
    initialState = OnBoardingState(),
    effectDispatcher = dispatchersProvider.immediate,
) {

    init {
        emitState(
            OnBoardingState(
                darkThemeToken = DarkThemeToken.parse(preferenceManager.designDarkThemeToken),
                appVersion = buildInfoProvider.toString(),
                showLocalDiffusionPage = isLocalDiffusionOnBoardingAvailable(),
            )
        )
    }

    override fun processIntent(intent: OnBoardingIntent) {
        when (intent) {
            OnBoardingIntent.Navigate -> navigate()
        }
    }

    private fun navigate() {
        preferenceManager.onBoardingComplete = true
        when (launchSource) {
            LaunchSource.SPLASH -> launch(dispatchersProvider.io) {
                runCatching { splashNavigationUseCase() }
                    .onSuccess { action ->
                        withContext(dispatchersProvider.immediate) {
                            router.postOnBoardingNavigation(action)
                        }
                    }
                    .onFailure { }
            }

            LaunchSource.SETTINGS -> router.navigateBack()
        }
    }
}
