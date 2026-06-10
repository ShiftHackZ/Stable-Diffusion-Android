package com.shifthackz.aisdv1.domain.usecase.downloadable

import com.shifthackz.aisdv1.domain.repository.DownloadableModelRepository

internal class DeleteModelUseCaseImpl(
    private val downloadableModelRepository: DownloadableModelRepository,
) : DeleteModelUseCase {

    override suspend fun invoke(id: String) {
        downloadableModelRepository.delete(id)
    }
}
