package com.shifthackz.aisdv1.domain.usecase.settings

import com.shifthackz.aisdv1.domain.entity.Configuration
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials
import com.shifthackz.aisdv1.domain.usecase.connectivity.TestSwarmUiConnectivityUseCase
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import java.util.concurrent.TimeUnit

internal class ConnectToSwarmUiUseCaseImpl(
    private val getConfigurationUseCase: GetConfigurationUseCase,
    private val setServerConfigurationUseCase : SetServerConfigurationUseCase,
    private val testSwarmUiConnectivityUseCase: TestSwarmUiConnectivityUseCase,
) : ConnectToSwarmUiUseCase {

    override fun invoke(
        url: String,
        credentials: AuthorizationCredentials,
    ): Single<Result<Unit>> {
        var configuration: Configuration? = null
        return getConfigurationUseCase()
            .map { originalConfiguration ->
                configuration = originalConfiguration
                originalConfiguration.copy(
                    source = ServerSource.SWARM_UI,
                    swarmUiUrl = url,
                    authCredentials = credentials,
                )
            }
            .flatMapCompletable(setServerConfigurationUseCase::invoke)
            .delay(3L, TimeUnit.SECONDS)
            .andThen(testSwarmUiConnectivityUseCase(url))
            .andThen(Single.just(Result.success(Unit)))
            .timeout(30L, TimeUnit.SECONDS)
            .onErrorResumeNext { t ->
                val chain = configuration?.let(setServerConfigurationUseCase::invoke)
                    ?: Completable.complete()

                chain.andThen(Single.just(Result.failure(t)))
            }
    }
}
