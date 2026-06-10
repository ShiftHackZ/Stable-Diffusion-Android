package com.shifthackz.aisdv1.domain.usecase.downloadable

import com.shifthackz.aisdv1.domain.repository.DownloadableModelRepository

internal class GetLocalOnnxModelsUseCaseImpl(
    private val downloadableModelRepository: DownloadableModelRepository,
) : GetLocalOnnxModelsUseCase {

    override suspend fun invoke() = downloadableModelRepository.getAllOnnx()
}
