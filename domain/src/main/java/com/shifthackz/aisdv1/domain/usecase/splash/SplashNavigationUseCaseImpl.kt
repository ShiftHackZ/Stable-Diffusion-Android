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
            true -> Action.LAUNCH_SERVER_SETUP//ToDo remove
            preferenceManager.forceSetupAfterUpdate -> {
                Action.LAUNCH_SERVER_SETUP
            }

            preferenceManager.source == ServerSource.LOCAL
                    || preferenceManager.source == ServerSource.HORDE
                    || preferenceManager.source == ServerSource.OPEN_AI
                    || preferenceManager.source == ServerSource.HUGGING_FACE -> {
                Action.LAUNCH_HOME
            }

            preferenceManager.serverUrl.isEmpty() -> {
                Action.LAUNCH_SERVER_SETUP
            }

            else -> Action.LAUNCH_HOME
        }
        emitter.onSuccess(action)
    }
}
