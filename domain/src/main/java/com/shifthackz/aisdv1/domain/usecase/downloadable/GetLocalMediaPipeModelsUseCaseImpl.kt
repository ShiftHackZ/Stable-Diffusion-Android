package com.shifthackz.aisdv1.domain.usecase.downloadable

import com.shifthackz.aisdv1.domain.repository.DownloadableModelRepository

internal class GetLocalMediaPipeModelsUseCaseImpl(
    private val downloadableModelRepository: DownloadableModelRepository,
    ) : GetLocalMediaPipeModelsUseCase {

    override fun invoke() = downloadableModelRepository.getAllMediaPipe()
}
