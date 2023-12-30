package com.shifthackz.aisdv1.domain.usecase.downloadable

import com.shifthackz.aisdv1.domain.repository.DownloadableModelRepository

internal class SelectLocalAiModelUseCaseImpl(
    private val downloadableModelRepository: DownloadableModelRepository,
) : SelectLocalAiModelUseCase {

    override fun invoke(id: String) = downloadableModelRepository.select(id)
}
