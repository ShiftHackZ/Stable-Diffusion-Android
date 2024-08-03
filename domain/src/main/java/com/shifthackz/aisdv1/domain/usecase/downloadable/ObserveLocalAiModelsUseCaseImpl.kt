package com.shifthackz.aisdv1.domain.usecase.downloadable

import com.shifthackz.aisdv1.domain.repository.DownloadableModelRepository

internal class ObserveLocalAiModelsUseCaseImpl(
    private val repository: DownloadableModelRepository,
) : ObserveLocalAiModelsUseCase {

    override fun invoke() = repository
        .observeAll()
        .distinctUntilChanged()
}
