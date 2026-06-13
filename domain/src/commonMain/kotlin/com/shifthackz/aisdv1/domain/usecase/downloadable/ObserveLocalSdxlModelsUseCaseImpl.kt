package com.shifthackz.aisdv1.domain.usecase.downloadable

import com.shifthackz.aisdv1.domain.repository.DownloadableModelRepository
import kotlinx.coroutines.flow.distinctUntilChanged

internal class ObserveLocalSdxlModelsUseCaseImpl(
    private val repository: DownloadableModelRepository,
) : ObserveLocalSdxlModelsUseCase {

    override fun invoke() = repository
        .observeAllSdxl()
        .distinctUntilChanged()
}
