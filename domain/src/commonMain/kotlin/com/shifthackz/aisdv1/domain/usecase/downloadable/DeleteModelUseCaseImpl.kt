package com.shifthackz.aisdv1.domain.usecase.downloadable

import com.shifthackz.aisdv1.domain.repository.DownloadableModelRepository

/**
 * Implements `DeleteModelUseCase` behavior in the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
internal class DeleteModelUseCaseImpl(
    /**
     * Exposes the `downloadableModelRepository` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val downloadableModelRepository: DownloadableModelRepository,
) : DeleteModelUseCase {

    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @param id identifier of the target entity.
     * @author Dmitriy Moroz
     */
    override suspend fun invoke(id: String) {
        downloadableModelRepository.delete(id)
    }
}
