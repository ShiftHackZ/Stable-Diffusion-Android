package com.shifthackz.aisdv1.domain.usecase.splash

import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.usecase.splash.SplashNavigationUseCase.Action
import io.reactivex.rxjava3.core.Single

internal class SplashNavigationUseCaseImpl(
    private val preferenceManager: PreferenceManager,
) : SplashNavigationUseCase {

    override fun invoke(): Single<Action> = Single.create { emitter ->
        val action = when {
            preferenceManager.forceSetupAfterUpdate -> {
                Action.LAUNCH_SERVER_SETUP
            }

            preferenceManager.serverUrl.isEmpty()
                    && preferenceManager.source == ServerSource.AUTOMATIC1111 -> {
                Action.LAUNCH_SERVER_SETUP
            }

            else -> Action.LAUNCH_HOME
        }
        emitter.onSuccess(action)
    }
}
