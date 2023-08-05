package com.shifthackz.aisdv1.domain.usecase.downloadable

import com.shifthackz.aisdv1.domain.repository.DownloadableModelRepository

internal class CheckDownloadedModelUseCaseImpl(
    private val downloadableModelRepository: DownloadableModelRepository,
) : CheckDownloadedModelUseCase {

    override fun invoke() = downloadableModelRepository.isModelDownloaded()
}
