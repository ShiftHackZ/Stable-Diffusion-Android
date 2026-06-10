package com.shifthackz.aisdv1.domain.usecase.splash

import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.preference.PreferenceManager

/**
 * Defines the `SplashNavigationUseCase` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
interface SplashNavigationUseCase {
    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @return Result produced by `invoke`.
     * @author Dmitriy Moroz
     */
    suspend operator fun invoke(): Action

    /**
     * Coordinates `Action` behavior in the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    enum class Action {
        LAUNCH_ONBOARDING,
        LAUNCH_SERVER_SETUP,
        LAUNCH_HOME;
    }
}

/**
 * Implements `SplashNavigationUseCase` behavior in the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
class SplashNavigationUseCaseImpl(
    /**
     * Exposes the `preferenceManager` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val preferenceManager: PreferenceManager,
) : SplashNavigationUseCase {

    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @return Result produced by `invoke`.
     * @author Dmitriy Moroz
     */
    override suspend fun invoke(): SplashNavigationUseCase.Action {
        return when {
            !preferenceManager.onBoardingComplete -> {
                SplashNavigationUseCase.Action.LAUNCH_ONBOARDING
            }

            preferenceManager.forceSetupAfterUpdate -> {
                SplashNavigationUseCase.Action.LAUNCH_SERVER_SETUP
            }

            preferenceManager.automatic1111ServerUrl.isEmpty() &&
                preferenceManager.source == ServerSource.AUTOMATIC1111 -> {
                SplashNavigationUseCase.Action.LAUNCH_SERVER_SETUP
            }

            else -> SplashNavigationUseCase.Action.LAUNCH_HOME
        }
    }
}
