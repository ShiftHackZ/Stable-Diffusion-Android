package com.shifthackz.aisdv1.domain.usecase.downloadable

import com.shifthackz.aisdv1.domain.repository.DownloadableModelRepository

/**
 * Implements `GetLocalOnnxModelsUseCase` behavior in the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
internal class GetLocalOnnxModelsUseCaseImpl(
    /**
     * Exposes the `downloadableModelRepository` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val downloadableModelRepository: DownloadableModelRepository,
) : GetLocalOnnxModelsUseCase {

    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    override suspend fun invoke() = downloadableModelRepository.getAllOnnx()
}
