package com.shifthackz.aisdv1.domain.usecase.downloadable

import com.shifthackz.aisdv1.domain.entity.LocalAiModel
import com.shifthackz.aisdv1.domain.repository.DownloadableModelRepository
import io.reactivex.rxjava3.core.Single

internal class GetLocalAiModelsUseCaseImpl(
    private val downloadableModelRepository: DownloadableModelRepository,
) : GetLocalAiModelsUseCase {

    override fun invoke(): Single<List<LocalAiModel>> = downloadableModelRepository.getAll()
}
