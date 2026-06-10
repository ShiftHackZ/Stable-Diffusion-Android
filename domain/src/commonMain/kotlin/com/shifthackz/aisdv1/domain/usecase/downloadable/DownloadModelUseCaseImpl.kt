package com.shifthackz.aisdv1.domain.usecase.downloadable

import com.shifthackz.aisdv1.domain.repository.DownloadableModelRepository

/**
 * Implements `DownloadModelUseCase` behavior in the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
internal class DownloadModelUseCaseImpl(
    /**
     * Exposes the `downloadableModelRepository` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val downloadableModelRepository: DownloadableModelRepository,
) : DownloadModelUseCase {

    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @param id identifier of the target entity.
     * @param url remote URL used by the operation.
     * @author Dmitriy Moroz
     */
    override fun invoke(id: String, url: String) = downloadableModelRepository.download(id, url)
}
