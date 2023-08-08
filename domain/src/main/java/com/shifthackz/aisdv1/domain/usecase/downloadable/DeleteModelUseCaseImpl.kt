package com.shifthackz.aisdv1.domain.usecase.downloadable

import com.shifthackz.aisdv1.domain.repository.DownloadableModelRepository

internal class DeleteModelUseCaseImpl(
    private val downloadableModelRepository: DownloadableModelRepository,
) : DeleteModelUseCase {

    override fun invoke() = downloadableModelRepository.delete()
}
