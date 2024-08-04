package com.shifthackz.aisdv1.domain.usecase.downloadable

import com.shifthackz.aisdv1.domain.repository.DownloadableModelRepository
import io.reactivex.rxjava3.core.Completable

internal class DeleteModelUseCaseImpl(
    private val downloadableModelRepository: DownloadableModelRepository,
) : DeleteModelUseCase {

    override fun invoke(id: String): Completable = downloadableModelRepository.delete(id)
}
