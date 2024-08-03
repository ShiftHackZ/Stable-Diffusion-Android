package com.shifthackz.aisdv1.domain.usecase.settings

import com.shifthackz.aisdv1.domain.entity.Configuration
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials
import com.shifthackz.aisdv1.domain.usecase.connectivity.TestHordeApiKeyUseCase
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import java.util.concurrent.TimeUnit

internal class ConnectToHordeUseCaseImpl(
    private val getConfigurationUseCase: GetConfigurationUseCase,
    private val setServerConfigurationUseCase : SetServerConfigurationUseCase,
    private val testHordeApiKeyUseCase: TestHordeApiKeyUseCase,
) : ConnectToHordeUseCase {

    override fun invoke(apiKey: String): Single<Result<Unit>> {
        var configuration: Configuration? = null
        return getConfigurationUseCase.invoke()
            .map { originalConfiguration ->
                configuration = originalConfiguration
                originalConfiguration.copy(
                    source = ServerSource.HORDE,
                    hordeApiKey = apiKey,
                    authCredentials = AuthorizationCredentials.None,
                )
            }
            .flatMapCompletable(setServerConfigurationUseCase::invoke)
            .delay(3L, TimeUnit.SECONDS)
            .andThen(testHordeApiKeyUseCase())
            .flatMap {
                if (it) Single.just(Result.success(Unit))
                else Single.error(IllegalStateException("Bad key"))
            }
            .onErrorResumeNext { t ->
                val chain = configuration?.let(setServerConfigurationUseCase::invoke)
                    ?: Completable.complete()

                chain.andThen(Single.just(Result.failure(t)))
            }
    }
}
