package com.shifthackz.aisdv1.domain.usecase.version

import com.shifthackz.aisdv1.domain.repository.AppVersionRepository
import io.reactivex.rxjava3.core.Single

internal class CheckAppVersionUpdateUseCaseImpl(
    private val repository: AppVersionRepository,
) : CheckAppVersionUpdateUseCase {

    override fun invoke() = Single
        .zip(repository.getActualVersion(), repository.getLocalVersion(), ::Pair)
        .map { (actualVer, localVer) ->
            if (localVer < actualVer) {
                CheckAppVersionUpdateUseCase.Result.NewVersionAvailable(actualVer)
            } else {
                CheckAppVersionUpdateUseCase.Result.NoUpdateNeeded
            }
        }
}
