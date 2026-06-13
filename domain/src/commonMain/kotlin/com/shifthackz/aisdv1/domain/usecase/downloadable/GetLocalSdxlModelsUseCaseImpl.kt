package com.shifthackz.aisdv1.domain.usecase.downloadable

import com.shifthackz.aisdv1.domain.repository.DownloadableModelRepository

internal class GetLocalSdxlModelsUseCaseImpl(
    private val downloadableModelRepository: DownloadableModelRepository,
) : GetLocalSdxlModelsUseCase {

    override suspend fun invoke() = downloadableModelRepository.getAllSdxl()
}
