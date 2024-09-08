package com.shifthackz.aisdv1.domain.usecase.downloadable

import com.shifthackz.aisdv1.domain.repository.DownloadableModelRepository

internal class ObserveLocalOnnxModelsUseCaseImpl(
    private val repository: DownloadableModelRepository,
) : ObserveLocalOnnxModelsUseCase {

    override fun invoke() = repository
        .observeAllOnnx()
        .distinctUntilChanged()
}
