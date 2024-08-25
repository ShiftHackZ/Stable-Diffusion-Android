package com.shifthackz.aisdv1.domain.usecase.settings

import com.shifthackz.aisdv1.domain.entity.ServerSource
import io.reactivex.rxjava3.core.Single

internal class ConnectToMediaPipeUseCaseImpl(
    private val getConfigurationUseCase: GetConfigurationUseCase,
    private val setServerConfigurationUseCase: SetServerConfigurationUseCase,
) : ConnectToMediaPipeUseCase {

    override fun invoke()  = getConfigurationUseCase()
        .map { originalConfiguration ->
            originalConfiguration.copy(
                source = ServerSource.LOCAL_GOOGLE_MEDIA_PIPE,
            )
        }
        .flatMapCompletable(setServerConfigurationUseCase::invoke)
        .andThen(Single.just(Result.success(Unit)))
        .onErrorResumeNext { t -> Single.just(Result.failure(t)) }

}
