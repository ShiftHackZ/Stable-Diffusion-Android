package com.shifthackz.aisdv1.domain.usecase.splash

import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.preference.PreferenceManager

interface SplashNavigationUseCase {
    suspend operator fun invoke(): Action

    enum class Action {
        LAUNCH_ONBOARDING,
        LAUNCH_SERVER_SETUP,
        LAUNCH_HOME;
    }
}

class SplashNavigationUseCaseImpl(
    private val preferenceManager: PreferenceManager,
) : SplashNavigationUseCase {

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
