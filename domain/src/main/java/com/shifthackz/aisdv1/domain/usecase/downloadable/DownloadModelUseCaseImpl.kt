package com.shifthackz.aisdv1.domain.usecase.downloadable

import com.shifthackz.aisdv1.domain.entity.DownloadState
import com.shifthackz.aisdv1.domain.repository.DownloadableModelRepository
import io.reactivex.rxjava3.core.Observable

internal class DownloadModelUseCaseImpl(
    private val downloadableModelRepository: DownloadableModelRepository,
) : DownloadModelUseCase {

    override fun invoke(id: String): Observable<DownloadState> = downloadableModelRepository.download(id)
}
