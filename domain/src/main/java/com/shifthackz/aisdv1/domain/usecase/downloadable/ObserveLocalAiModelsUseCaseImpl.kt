package com.shifthackz.aisdv1.domain.usecase.downloadable

import com.shifthackz.aisdv1.domain.entity.LocalAiModel
import com.shifthackz.aisdv1.domain.repository.DownloadableModelRepository
import io.reactivex.rxjava3.core.Flowable

internal class ObserveLocalAiModelsUseCaseImpl(
    private val repository: DownloadableModelRepository,
) : ObserveLocalAiModelsUseCase {

    override fun invoke(): Flowable<List<LocalAiModel>> = repository
        .observeAll()
        .distinctUntilChanged()
}
