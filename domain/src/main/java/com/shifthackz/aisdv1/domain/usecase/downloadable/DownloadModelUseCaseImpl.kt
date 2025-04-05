package com.shifthackz.aisdv1.domain.usecase.downloadable

import com.shifthackz.aisdv1.domain.repository.DownloadableModelRepository

internal class DownloadModelUseCaseImpl(
    private val downloadableModelRepository: DownloadableModelRepository,
) : DownloadModelUseCase {

    override fun invoke(id: String, url: String) = downloadableModelRepository.download(id, url)
}
