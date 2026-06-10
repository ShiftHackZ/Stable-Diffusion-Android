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

/**
 * Coordinates `OnBoardingViewModel` behavior in the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
class OnBoardingViewModel(
    /**
     * Exposes the `launchSource` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val launchSource: LaunchSource,
    /**
     * Exposes the `dispatchersProvider` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val dispatchersProvider: DispatchersProvider,
    /**
     * Exposes the `router` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val router: OnBoardingRouter,
    /**
     * Exposes the `splashNavigationUseCase` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val splashNavigationUseCase: SplashNavigationUseCase,
    /**
     * Exposes the `preferenceManager` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val preferenceManager: PreferenceManager,
    /**
     * Exposes the `buildInfoProvider` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
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
