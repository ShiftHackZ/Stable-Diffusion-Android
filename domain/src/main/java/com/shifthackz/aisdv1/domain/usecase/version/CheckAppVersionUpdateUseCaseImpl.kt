package com.shifthackz.aisdv1.domain.usecase.version

import com.shifthackz.aisdv1.core.common.appbuild.BuildVersion
import com.shifthackz.aisdv1.domain.repository.AppVersionRepository
import io.reactivex.rxjava3.core.Single

internal class CheckAppVersionUpdateUseCaseImpl(
    private val repository: AppVersionRepository,
) : CheckAppVersionUpdateUseCase {

    private val remoteVersionProducer: () -> Single<BuildVersion> = {
        repository
            .getActualVersion()
            .onErrorReturn { BuildVersion() }
    }

    private val localVersionProducer: () -> Single<BuildVersion> = {
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
