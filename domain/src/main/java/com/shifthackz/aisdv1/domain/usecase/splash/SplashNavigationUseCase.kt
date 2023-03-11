package com.shifthackz.aisdv1.domain.usecase.splash

import io.reactivex.rxjava3.core.Single

interface SplashNavigationUseCase {
    operator fun invoke(): Single<Action>

    enum class Action {
        LAUNCH_ONBOARDING,
        LAUNCH_SERVER_SETUP,
        LAUNCH_HOME;
    }
}
