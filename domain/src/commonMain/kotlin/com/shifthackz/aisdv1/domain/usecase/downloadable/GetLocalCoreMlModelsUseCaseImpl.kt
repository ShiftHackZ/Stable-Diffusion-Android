package com.shifthackz.aisdv1.domain.usecase.downloadable

import com.shifthackz.aisdv1.domain.repository.DownloadableModelRepository

/**
 * Implements `GetLocalCoreMlModelsUseCase` behavior in the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
internal class GetLocalCoreMlModelsUseCaseImpl(
    /**
     * Exposes the `downloadableModelRepository` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val downloadableModelRepository: DownloadableModelRepository,
) : GetLocalCoreMlModelsUseCase {

    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    override suspend fun invoke() = downloadableModelRepository.getAllCoreMl()
}
