package com.shifthackz.aisdv1.domain.usecase.settings

import com.shifthackz.aisdv1.core.common.reactive.retryWithDelay
import com.shifthackz.aisdv1.domain.entity.Configuration
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials
import com.shifthackz.aisdv1.domain.usecase.caching.DataPreLoaderUseCase
import com.shifthackz.aisdv1.domain.usecase.connectivity.TestConnectivityUseCase
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import java.util.concurrent.TimeUnit

internal class ConnectToA1111UseCaseImpl(
    private val getConfigurationUseCase: GetConfigurationUseCase,
    private val setServerConfigurationUseCase : SetServerConfigurationUseCase,
    private val testConnectivityUseCase: TestConnectivityUseCase,
    private val dataPreLoaderUseCase: DataPreLoaderUseCase,
) : ConnectToA1111UseCase {

    override fun invoke(
        url: String,
        isDemo: Boolean,
        credentials: AuthorizationCredentials
    ): Single<Result<Unit>> {
        var configuration: Configuration? = null
        return getConfigurationUseCase()
            .map { originalConfiguration ->
                configuration = originalConfiguration
                originalConfiguration.copy(
                    source = ServerSource.AUTOMATIC1111,
                    serverUrl = url,
                    demoMode = isDemo,
                    authCredentials = credentials,
                )
            }
            .flatMapCompletable(setServerConfigurationUseCase::invoke)
            .andThen(testConnectivityUseCase(url))
            .andThen(
                Observable
                    .timer(5L, TimeUnit.SECONDS)
                    .flatMapCompletable {
                        dataPreLoaderUseCase()
                            .retryWithDelay(3L, 1L, TimeUnit.SECONDS)
                    }
            )
            .andThen(Single.just(Result.success(Unit)))
            .timeout(30L, TimeUnit.SECONDS)
            .onErrorResumeNext { t ->
                val chain = configuration?.let(setServerConfigurationUseCase::invoke)
                    ?: Completable.complete()

                chain.andThen(Single.just(Result.failure(t)))
            }
    }
}
