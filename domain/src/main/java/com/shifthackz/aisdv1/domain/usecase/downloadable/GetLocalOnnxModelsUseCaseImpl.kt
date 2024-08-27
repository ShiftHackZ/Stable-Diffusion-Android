package com.shifthackz.aisdv1.domain.usecase.downloadable

import com.shifthackz.aisdv1.domain.repository.DownloadableModelRepository

internal class GetLocalOnnxModelsUseCaseImpl(
    private val downloadableModelRepository: DownloadableModelRepository,
) : GetLocalOnnxModelsUseCase {

    override fun invoke() = downloadableModelRepository.getAllOnnx()
}
