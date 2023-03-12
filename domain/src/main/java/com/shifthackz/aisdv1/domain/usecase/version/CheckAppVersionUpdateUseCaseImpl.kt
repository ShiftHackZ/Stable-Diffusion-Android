package com.shifthackz.aisdv1.domain.usecase.version

import com.shifthackz.aisdv1.domain.entity.AppVersion
import com.shifthackz.aisdv1.domain.repository.AppVersionRepository
import io.reactivex.rxjava3.core.Single

internal class CheckAppVersionUpdateUseCaseImpl(
    private val repository: AppVersionRepository,
) : CheckAppVersionUpdateUseCase {

    private val remoteVersionProducer: () -> Single<AppVersion> = {
        repository
            .getActualVersion()
            .onErrorReturn { AppVersion() }
    }

    private val localVersionProducer: () -> Single<AppVersion> = {
        repository.getLocalVersion()
    }

    override fun invoke() = Single
        .zip(remoteVersionProducer(), localVersionProducer(), ::Pair)
        .map { (actualVer, localVer) ->
            if (localVer < actualVer) {
                CheckAppVersionUpdateUseCase.Result.NewVersionAvailable(actualVer)
            } else {
                CheckAppVersionUpdateUseCase.Result.NoUpdateNeeded
            }
        }
}
